package com.mlorenzo.spring5reactivemongorecipeapp.services;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;

import reactor.core.publisher.Mono;

public interface IngredientService {
    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command);
    Mono<Void> deleteById(String recipeId, String idToDelete);
}
