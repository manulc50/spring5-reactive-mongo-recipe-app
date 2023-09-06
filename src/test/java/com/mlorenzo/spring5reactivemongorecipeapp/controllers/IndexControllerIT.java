package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import reactor.core.publisher.Flux;

@RunWith(SpringRunner.class)
// Nota: Por defecto, esta anotación hace que también se procesen las plantillas(En nuestro caso, plantillas Thymeleaf)
@WebFluxTest(controllers = IndexController.class)
public class IndexControllerIT {
	
	@Autowired
	WebTestClient webTestClient;
	
	@MockBean
	RecipeService recipeService;
	
	@Test
    public void testGetIndexPage() throws Exception {
		//given
    	when(recipeService.getRecipes()).thenReturn(Flux.empty());
    	//when
        webTestClient.get().uri("/")
        	.exchange()
        	//then
        	.expectStatus().isOk();
    }

}
