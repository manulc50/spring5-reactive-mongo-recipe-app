package com.mlorenzo.spring5reactivemongorecipeapp.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.RecipeCommandToRecipe;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.RecipeToRecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RecipeServiceImplTest {
    RecipeServiceImpl recipeService;
    
    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        recipeService = new RecipeServiceImpl(recipeReactiveRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }
    
    @Test
    public void getRecipeCommandByIdTest() throws Exception {
    	Recipe recipeReturned = new Recipe();
        recipeReturned.setId("1");
        RecipeCommand commandReturned = new RecipeCommand();
        commandReturned.setId(recipeReturned.getId());
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipeReturned));
        when(recipeToRecipeCommand.convert(any(Recipe.class))).thenReturn(commandReturned);
        StepVerifier.create(recipeService.findCommandById("1"))
        	.expectSubscription()
        	.assertNext(recipeCommand -> assertNotNull("Null recipe returned", recipeCommand))
        	.verifyComplete();
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
        verify(recipeToRecipeCommand).convert(any(Recipe.class));
    }
    
    @Test
    public void getRecipeCommandByIdNotFound() throws Exception {
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.empty());   
        //should go boom
        StepVerifier.create(recipeService.findCommandById("1"))
        	.expectSubscription()
        	.expectError(NotFoundException.class)
        	.verify();
    }
    
    @Test
    public void testGetRecipes() throws Exception {
    	Recipe recipe1 = new Recipe();
		recipe1.setId("1");
		Recipe recipe2 = new Recipe();
		recipe2.setId("2");
		RecipeCommand command1 = new RecipeCommand();
		command1.setId(recipe1.getId());
		RecipeCommand command2 = new RecipeCommand();
		command2.setId(recipe2.getId());
		when(recipeReactiveRepository.findAll()).thenReturn(Flux.just(recipe1, recipe2));
		when(recipeToRecipeCommand.convert(any(Recipe.class))).thenReturn(command1).thenReturn(command2);
		StepVerifier.create(recipeService.getRecipes())
			.expectSubscription()
			.expectNextCount(2)
			.expectComplete()
			.verify();
		// Si no se indica el número de llamadas en el método "times", por defecto es 1
		verify(recipeReactiveRepository, times(1)).findAll();
		verify(recipeToRecipeCommand, times(2)).convert(any(Recipe.class));
    }
    
    @Test
    public void testDeleteById() throws Exception {
    	//given
        String idToDelete = "2";
        //when
        recipeService.deleteById(idToDelete);
        //no 'when', since method has void return type
        //then
        verify(recipeReactiveRepository, times(1)).deleteById(anyString());
    }
}