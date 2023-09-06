package com.mlorenzo.spring5reactivemongorecipeapp.services;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecipeService {
    Flux<RecipeCommand> getRecipes();
    Mono<RecipeCommand> findCommandById(String id);
    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command);
    Mono<Void> deleteById(String idToDelete);
}
