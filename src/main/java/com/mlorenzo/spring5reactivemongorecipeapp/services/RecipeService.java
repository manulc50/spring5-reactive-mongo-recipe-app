package com.mlorenzo.spring5reactivemongorecipeapp.services;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecipeService {
    Flux<Recipe> getRecipes();
    Mono<Recipe> findById(String id);
    Mono<RecipeCommand> findCommandById(String id);
    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command);
    Mono<Void> deleteById(String idToDelete);
}
