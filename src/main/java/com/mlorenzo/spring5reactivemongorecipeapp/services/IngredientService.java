package com.mlorenzo.spring5reactivemongorecipeapp.services;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IngredientService {
	Flux<IngredientCommand> getIngredients(String recipeId);
	Mono<IngredientCommand> createNewIngredient(String recipeId);
    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command);
    Mono<Void> deleteById(String recipeId, String idToDelete);
}
