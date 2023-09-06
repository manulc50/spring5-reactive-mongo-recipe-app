package com.mlorenzo.spring5reactivemongorecipeapp.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

public class ImageServiceImplTest {

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    ImageService imageService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        imageService = new ImageServiceImpl(recipeReactiveRepository);
    }
    
    @Test
    public void testSaveImageFile() throws Exception {
    	//given
    	byte[] imageBytes = "fake image text".getBytes(StandardCharsets.UTF_8);
    	FilePart filePart = new FilePart() {
			
			@Override
			public String name() {
				return "imagefile";
			}
			
			@Override
			public HttpHeaders headers() {
				return HttpHeaders.EMPTY;
			}
			
			@Override
			public Flux<DataBuffer> content() {
				 return DataBufferUtils.read(
	                        new ByteArrayResource(imageBytes),
	                        	new DefaultDataBufferFactory(), 1024);
			}
			
			@Override
			public Mono<Void> transferTo(Path dest) {
				return Mono.empty();
			}
			
			@Override
			public String filename() {
				return "testing.jpg";
			}
		};
    	String id = "1";
        Recipe recipe = new Recipe();
        recipe.setId(id);
        Recipe savedRecipe = new Recipe();
        savedRecipe.setId(recipe.getId());
        savedRecipe.setImage(imageBytes);
        ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass(Recipe.class);
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeReactiveRepository.save(any(Recipe.class))).thenReturn(Mono.just(savedRecipe));
        //when
        StepVerifier.create(imageService.saveImageFile(id, filePart))
        	//then
        	.expectSubscription()
        	.expectComplete()
        	.verify();
        verify(recipeReactiveRepository, times(1)).save(argumentCaptor.capture());
        Recipe recipeCaptor = argumentCaptor.getValue();
        assertEquals(savedRecipe.getImage().length, recipeCaptor.getImage().length);
        
    }

}