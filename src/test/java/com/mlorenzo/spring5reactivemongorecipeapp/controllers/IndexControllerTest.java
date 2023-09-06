package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

// Anotación para poder usar Mockito en esta clase de pruebas
@RunWith(MockitoJUnitRunner.class) // Otra opción a esta anotación es usar la expresión o línea "MockitoAnnotations.initMocks(this);" en el método "setUp"
public class IndexControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    IndexController controller;
    
    @Before
    public void setUp() {
    	controller = new IndexController(recipeService);
    }

    @Test
    public void getIndexPage() throws Exception {
        //given
    	RecipeCommand recipeCommand1 = new RecipeCommand();
		recipeCommand1.setId("1");
		RecipeCommand recipeCommand2 = new RecipeCommand();
		recipeCommand2.setId("2");
        when(recipeService.getRecipes()).thenReturn(Flux.just(recipeCommand1, recipeCommand2));
        ArgumentCaptor<Flux<RecipeCommand>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);
        //when
        String viewName = controller.getIndexPage(model);
        //then
        assertEquals("index", viewName);
        verify(recipeService, times(1)).getRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
        StepVerifier.create(argumentCaptor.getValue())
        	.expectNextCount(2)
        	.verifyComplete();
    }

}