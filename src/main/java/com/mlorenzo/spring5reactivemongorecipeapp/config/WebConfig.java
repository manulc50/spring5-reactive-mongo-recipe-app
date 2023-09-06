package com.mlorenzo.spring5reactivemongorecipeapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class WebConfig {
	
	// La implementación de la interfaz "RecipeService" es inyectada automáticamente por Spring en el parámetro de entrada de este método al ser un componente de Spring debido a la anotación @Service
	@Bean
	public RouterFunction<?> routes(RecipeService recipeService) {
		return RouterFunctions.route(GET("/api/recipes"),
				serverRequest -> ServerResponse.ok()
									.contentType(MediaType.APPLICATION_JSON)
									.body(recipeService.getRecipes(),RecipeCommand.class));
	}
}
