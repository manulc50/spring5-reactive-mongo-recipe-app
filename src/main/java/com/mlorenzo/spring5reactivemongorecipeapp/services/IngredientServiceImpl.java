package com.mlorenzo.spring5reactivemongorecipeapp.services;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientCommandToIngredient;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientToIngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Ingredient;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.UnitOfMeasureReactiveRepository;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeReactiveRepository recipeReactiveRepository, UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
    }

	@Override
	public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
		/*return recipeReactiveRepository.findById(recipeId)
				.map(recipe -> recipe.getIngredients().stream()
        				.filter(ingredient -> ingredient.getId().equals(ingredientId))
        				.findFirst())
				.filter(Optional::isPresent) // Versión simplificada dela expresión "ingredientOptional -> ingredientOptional.isPresent()"
				.map(ingredientOptional -> {
					IngredientCommand ingredientCommand = ingredientToIngredientCommand.convert(ingredientOptional.get());
					ingredientCommand.setRecipeId(recipeId);
					return ingredientCommand;
				});*/
		// Esta forma es una alternativa a la anterior mucho más reactiva
		return recipeReactiveRepository.findById(recipeId)
				.flatMapIterable(Recipe::getIngredients) // Versión simplificada de la expresión "recipe -> recipe.getIngredients()" 
				.filter(ingredient -> ingredient.getId().equals(ingredientId))
				.single()
				.map(ingredient -> {
					IngredientCommand ingredientCommand = ingredientToIngredientCommand.convert(ingredient);
					ingredientCommand.setRecipeId(recipeId);
					return ingredientCommand;
				});
	}

	@Override
	public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
		Recipe recipe = recipeReactiveRepository.findById(command.getRecipeId()).block();
        if(recipe == null){
            //todo toss error if not found!
            log.error("Recipe not found for id: " + command.getRecipeId());
            return Mono.just(new IngredientCommand());
        } else {
            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();
            if(ingredientOptional.isPresent()){
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());
                ingredientFound.setUom(unitOfMeasureReactiveRepository.findById(command.getUom().getId()).block());
            } else {
                //add new Ingredient
                Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                //ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }
            Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();
            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                    .findFirst();
            //check by description
            if(!savedIngredientOptional.isPresent()){
                //not totally safe... But best guess
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                        .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                        .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
                        .findFirst();
            }
            //todo check for fail
            //enhance with id value
            IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
            ingredientCommandSaved.setRecipeId(recipe.getId());
            return Mono.just(ingredientCommandSaved);
        }
	}

	@Override
	public Mono<Void> deleteById(String recipeId, String idToDelete) {
		log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);
		recipeReactiveRepository.findById(recipeId)
			.subscribe(recipe -> {
				if(recipe != null){
		            log.debug("found recipe");
		            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
		                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
		                    .findFirst();
		            if(ingredientOptional.isPresent()){
		                log.debug("found Ingredient");
		                recipe.getIngredients().remove(ingredientOptional.get()); 
		                recipeReactiveRepository.save(recipe).subscribe();   
		            }
		        } else {
		            log.debug("Recipe Id Not found. Id:" + recipeId);
		        }
		});
		/*Recipe recipe = recipeReactiveRepository.findById(recipeId).block();
        if(recipe != null){
            log.debug("found recipe");
            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst();
            if(ingredientOptional.isPresent()){
                log.debug("found Ingredient");
                recipe.getIngredients().remove(ingredientOptional.get());
                recipeReactiveRepository.save(recipe).block();   
            }
        } else {
            log.debug("Recipe Id Not found. Id:" + recipeId);
        }*/
        return Mono.empty();
	}
}
