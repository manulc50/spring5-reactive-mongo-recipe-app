package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import reactor.core.publisher.Flux;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.ui.Model;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Ignore
@SpringBootTest
@AutoConfigureWebTestClient
public class IndexControllerTest {
	
	@Autowired
	WebTestClient webTestClient;

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    IndexController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new IndexController(recipeService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    public void testMockMVC() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setId("1");
        Recipe recipe2 = new Recipe();
        recipe2.setId("2");
        webTestClient.get().uri("/")
        	.exchange()
        	.expectStatus().isOk()
        	.expectBody();  
    }

    @Test
    public void getIndexPage() throws Exception {
        //given
        Recipe recipe1 = new Recipe();
        recipe1.setId("1");
        Recipe recipe2 = new Recipe();
        recipe2.setId("2");
        when(recipeService.getRecipes()).thenReturn(Flux.just(recipe1,recipe2));
        ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        //when
        String viewName = controller.getIndexPage(model);
        //then
        assertEquals("index", viewName);
        verify(recipeService, times(1)).getRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
        List<Recipe> listInController = argumentCaptor.getValue();
        assertEquals(2, listInController.size());
    }

}