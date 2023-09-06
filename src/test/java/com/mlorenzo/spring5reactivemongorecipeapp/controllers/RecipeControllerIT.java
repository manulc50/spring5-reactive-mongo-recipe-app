package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.services.CategoryService;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
//Nota: Por defecto, esta anotación hace que también se procesen las plantillas(En nuestro caso, plantillas Thymeleaf)
@WebFluxTest(controllers = RecipeController.class)
public class RecipeControllerIT {
	
	@Autowired
	WebTestClient webTestClient;

    @MockBean
    RecipeService recipeService;
    
    @MockBean
    CategoryService categoryService;

    @Test
    public void testGetRecipe() throws Exception {
    	RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipeCommand));
        webTestClient.get().uri("/recipe/1/show")
        	.exchange()
        	.expectStatus().isOk();
    }

    @Test
    public void testGetRecipeNotFound() throws Exception {
    	when(recipeService.findCommandById(anyString())).thenThrow(NotFoundException.class);
    	webTestClient.get().uri("/recipe/1/show")
    		.exchange()
    		.expectStatus().isNotFound();
    }

    @Test
    public void testGetNewRecipeForm() throws Exception {
    	webTestClient.get().uri("/recipe/new")
			.exchange()
			.expectStatus().isOk();
    }

    @Test
    public void testPostNewRecipeForm() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");
        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));
        webTestClient.post().uri("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("id", "")
                		.with("description", "some string")
                		.with("directions", "some directions"))
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    public void testPostNewRecipeFormValidationFail() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");
        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));
        webTestClient.post().uri("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("id", "")
                		.with("cookTime", "3000"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetUpdateView() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));
        webTestClient.get().uri("/recipe/1/update")
	        .exchange()
	        .expectStatus().isOk();
    }

    @Test
    public void testDeleteAction() throws Exception {
    	when(recipeService.deleteById(anyString())).thenReturn(Mono.empty());
    	webTestClient.get().uri("/recipe/1/delete")
	    	.exchange()
	        .expectStatus().is3xxRedirection();
        verify(recipeService, times(1)).deleteById(anyString());
    }
}