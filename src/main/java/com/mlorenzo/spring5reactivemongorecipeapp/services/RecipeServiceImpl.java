package com.mlorenzo.spring5reactivemongorecipeapp.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.RecipeCommandToRecipe;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.RecipeToRecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;

@AllArgsConstructor
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

	@Override
	public Flux<RecipeCommand> getRecipes() {
		log.debug("I'm in the service");
        return recipeReactiveRepository.findAll()
        		// Versión simplificada de la expresión "recipe -> recipeToRecipeCommand.convert(recipe)"
        		.map(recipeToRecipeCommand::convert);
	}

	@Override
	public Mono<RecipeCommand> findCommandById(String id) {
		return recipeReactiveRepository.findById(id)
				.switchIfEmpty(Mono.error(new NotFoundException("Recipe Not Found. For ID value: " + id )))
				// Versión simplificada de la expresión "recipe -> recipeToRecipeCommand.convert(recipe)"
        		.map(recipeToRecipeCommand::convert);
	}

	@Override
	public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
		return recipeReactiveRepository.save(recipeCommandToRecipe.convert(command))
				.doOnNext(savedRecipe -> log.debug("Saved RecipeId:" + savedRecipe.getId()))
				// Versión simplificada de la expresión "savedRecipe -> recipeToRecipeCommand.convert(savedRecipe)"
				.map(recipeToRecipeCommand::convert);
	}

	@Override
	public Mono<Void> deleteById(String idToDelete) {
		return recipeReactiveRepository.deleteById(idToDelete);
	}
}
