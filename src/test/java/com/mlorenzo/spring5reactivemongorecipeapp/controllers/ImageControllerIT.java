package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.services.ImageService;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
//Nota: Por defecto, esta anotación hace que también se procesen las plantillas(En nuestro caso, plantillas Thymeleaf)
@WebFluxTest(controllers = ImageController.class)
public class ImageControllerIT {

	@Autowired
	WebTestClient webTestClient;
	
    @MockBean
    ImageService imageService;

    @MockBean
    RecipeService recipeService;

    @Test
    public void getImageForm() throws Exception {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId("1");
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));
        //when
        webTestClient.get().uri("/recipe/1/image")
        	.exchange()
        	//then
            .expectStatus().isOk();
        verify(recipeService, times(1)).findCommandById(anyString());
    }

    @Test
    public void handleImagePost() throws Exception {
    	//given
		MultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt",
				"text/plain", "fake image text".getBytes());
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("imagefile", multipartFile.getResource());
		when(imageService.saveImageFile(anyString(), any(FilePart.class))).thenReturn(Mono.empty());
        //when
        webTestClient.post().uri("/recipe/1/image")
        	.body(BodyInserters.fromMultipartData(builder.build()))
        	.exchange()
        	//then
        	.expectStatus().is3xxRedirection();
        verify(imageService, times(1)).saveImageFile(anyString(), any(FilePart.class));
    }


    @Test
    public void renderImageFromDB() throws Exception {
    	//given
        String s = "fake image text";
        when(imageService.getImage(anyString())).thenReturn(Mono.just(s.getBytes()));
        //when
        EntityExchangeResult<byte[]> response = webTestClient.get().uri("/recipe/1/recipeimage")
        	.exchange()
        	//then
        	.expectStatus().isOk()
        	.expectBody(byte[].class).returnResult();
        byte[] reponseBytes = response.getResponseBodyContent();
        assertEquals(s.getBytes().length, reponseBytes.length);
    }

}