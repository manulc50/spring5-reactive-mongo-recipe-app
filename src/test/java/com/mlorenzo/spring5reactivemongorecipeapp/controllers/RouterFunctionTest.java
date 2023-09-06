package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.config.WebConfig;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import reactor.core.publisher.Flux;

public class RouterFunctionTest {
	@Mock
	RecipeService recipeService;
	
	WebTestClient webTestClient;
	
	@Before
	public void setUp() throws Exception{
		MockitoAnnotations.initMocks(this);
		WebConfig webConfig = new WebConfig();
		RouterFunction<?> routerFunction = webConfig.routes(recipeService);
		webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
	}
	
	@Test
	public void testGetRecipes() throws Exception{
		when(recipeService.getRecipes()).thenReturn(Flux.empty());
		webTestClient.get().uri("/api/recipes")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk();
	}
	
	@Test
	public void testGetRecipesWithData() throws Exception{
		when(recipeService.getRecipes()).thenReturn(Flux.just(new RecipeCommand()));
		webTestClient.get().uri("/api/recipes")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Recipe.class);
	}

}
