package com.mlorenzo.spring5reactivemongorecipeapp.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.UnitOfMeasureCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientToIngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Ingredient;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientCommandToIngredient;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.UnitOfMeasureCommandToUnitOfMeasure;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {
	private final RecipeReactiveRepository recipeReactiveRepository;
	private final IngredientToIngredientCommand ingrToIngrCommandConverter;
	private final IngredientCommandToIngredient ingrCommandToIngrConverter;
	private final UnitOfMeasureCommandToUnitOfMeasure uomCommandToUomConverter;
	
	@Override
	public Flux<IngredientCommand> getIngredients(String recipeId) {
		return recipeReactiveRepository.findById(recipeId)
				// Versión simplificada de la expresión "recipe -> recipe.getIngredients()" 
				.flatMapIterable(Recipe::getIngredients)
				// Versión simplificada de la expresión "ingredient -> ingrToIngrCommandConverter.convert(ingredient)" 
				.map(ingrToIngrCommandConverter::convert);
	}
	
	@Override
	public Mono<IngredientCommand> createNewIngredient(String recipeId) {
		return recipeReactiveRepository.findById(recipeId)
			.map(recipe -> {
				IngredientCommand ingredientCommand = new IngredientCommand();
		        ingredientCommand.setRecipeId(recipe.getId());
		        ingredientCommand.setId(UUID.randomUUID().toString());
		        ingredientCommand.setUom(new UnitOfMeasureCommand());
		        return ingredientCommand;
			});
	}

	@Override
	public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
		return recipeReactiveRepository.findById(recipeId)
				// Versión simplificada de la expresión "recipe -> recipe.getIngredients()" 
				.flatMapIterable(Recipe::getIngredients)
				.filter(ingredient -> ingredient.getId().equals(ingredientId))
				.single()
				.switchIfEmpty(Mono.error(new NotFoundException("Ingredient Not Found for id value: " + ingredientId)))
				.doOnError(error -> log.error("Ingredient id not found: " + ingredientId))
				.map(ingredient -> {
					IngredientCommand ingredientCommand = ingrToIngrCommandConverter.convert(ingredient);
					ingredientCommand.setRecipeId(recipeId);
					return ingredientCommand;
				});
	}

	@Override
	public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
		return recipeReactiveRepository.findById(command.getRecipeId())
				.flatMap(recipe -> {
					Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
			                .filter(ingredient -> ingredient.getId().equals(command.getId()))
			                .findFirst();
			        //update existing ingredient
			        if(ingredientOptional.isPresent()){
			            Ingredient ingredientFound = ingredientOptional.get();
			            ingredientFound.setDescription(command.getDescription());
			            ingredientFound.setAmount(command.getAmount());
			            ingredientFound.setUom(uomCommandToUomConverter.convert(command.getUom()));
			        }
			        //add new ingredient
			        else
			        	recipe.getIngredients().add(ingrCommandToIngrConverter.convert(command));
			        return recipeReactiveRepository.save(recipe);
				})
				.map(savedRecipe -> {
					// case of creating a new ingredient and updating an ingredient
			        Ingredient savedIngredient = savedRecipe.getIngredients().stream()
			                .filter(recipeIngredient -> recipeIngredient.getId().equals(command.getId()))
			                .findFirst().get();
			        IngredientCommand ingredientCommand = ingrToIngrCommandConverter.convert(savedIngredient);
			        ingredientCommand.setRecipeId(savedRecipe.getId());
			        return ingredientCommand;
				});
	}

	@Override
	public Mono<Void> deleteById(String recipeId, String idToDelete) {
		log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);
		return recipeReactiveRepository.findById(recipeId)
				.flatMap(recipe -> {
					Optional<Ingredient> removedIngredientOptional = recipe.getIngredients().stream()
			        		.filter(ingredient -> ingredient.getId().equals(idToDelete))
			        		.findFirst();
			        if (removedIngredientOptional.isPresent()){
			        	Ingredient removedIngredient = removedIngredientOptional.get();
			            recipe.getIngredients().remove(removedIngredient);
			            return recipeReactiveRepository.save(recipe);
			        }
			        return Mono.empty();
				})
				.then();
	}

}
