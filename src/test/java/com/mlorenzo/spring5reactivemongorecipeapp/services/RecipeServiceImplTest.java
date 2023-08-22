package com.mlorenzo.spring5reactivemongorecipeapp.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

import java.util.List;

import static org.junit.Assert.*;
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
    public void testGetRecipeById() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Mono<Recipe> recipeMono = Mono.just(recipe);
        when(recipeReactiveRepository.findById(anyString())).thenReturn(recipeMono);
        Recipe recipeReturned = recipeService.findById("1").block();
        assertNotNull("Null recipe returned", recipeReturned);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }
    
    @Test(expected = NotFoundException.class)
    public void testGetRecipeByIdNotFound() throws Exception {
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.empty());   
        //should go boom
       	recipeService.findById("1").block();  
    }
    
    @Test
    public void testGetRecipeCommandById() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Mono<Recipe> recipeMono = Mono.just(recipe);
        when(recipeReactiveRepository.findById(anyString())).thenReturn(recipeMono);
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");
        when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);
        RecipeCommand commandById = recipeService.findCommandById("1").block();
        assertNotNull("Null recipe returned", commandById);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }
    
    @Test
    public void testGetRecipes() throws Exception {
        when(recipeReactiveRepository.findAll()).thenReturn(Flux.just(new Recipe()));
        List<Recipe> recipes = recipeService.getRecipes().collectList().block();
        assertEquals(recipes.size(), 1);
        verify(recipeReactiveRepository, times(1)).findAll();
        verify(recipeReactiveRepository, never()).findById(anyString());
    }
    
    @Test
    public void testDeleteById() throws Exception {
        //given
        String idToDelete = "2";
        //when
        recipeService.deleteById(idToDelete);
        //no 'when', since method has Mono<Void> return type
        //then
        verify(recipeReactiveRepository, times(1)).deleteById(anyString());
    }
}