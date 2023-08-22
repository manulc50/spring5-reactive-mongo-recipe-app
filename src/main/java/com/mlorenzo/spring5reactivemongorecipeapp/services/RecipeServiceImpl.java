package com.mlorenzo.spring5reactivemongorecipeapp.services;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.RecipeCommandToRecipe;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.RecipeToRecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeReactiveRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

	@Override
	public Flux<Recipe> getRecipes() {
		log.debug("I'm in the service");
        return recipeReactiveRepository.findAll();
	}

	@Override
	public Mono<Recipe> findById(String id) {
		return recipeReactiveRepository.findById(id)
				.switchIfEmpty(Mono.error(new NotFoundException("Recipe Not Found. For ID value: " + id )));
	}

	@Override
	public Mono<RecipeCommand> findCommandById(String id) {
		return findById(id)
				.map(recipe -> {
					RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);	
					//enhance command object with id value
		            recipeCommand.getIngredients().forEach(rc -> {
		                rc.setRecipeId(recipeCommand.getId());
		            });
			        return recipeCommand;
				});
	}

	@Override
	public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
		Recipe detachedRecipe = recipeCommandToRecipe.convert(command);
		return recipeReactiveRepository.save(detachedRecipe)
				.doOnNext(savedRecipe -> log.debug("Saved RecipeId:" + savedRecipe.getId()))
				.map(recipeToRecipeCommand::convert); // Versión simplificada de la expresión "savedRecipe -> recipeToRecipeCommand.convert(savedRecipe)"
	}

	@Override
	public Mono<Void> deleteById(String idToDelete) {
		return recipeReactiveRepository.deleteById(idToDelete);
	}
}
