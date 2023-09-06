package com.mlorenzo.spring5reactivemongorecipeapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.RecipeToRecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecipeServiceIT {
    public static final String NEW_DESCRIPTION = "New Description";

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @Autowired
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Test
    public void testSaveOfDescription() throws Exception {
    	//given
    	Mono<Recipe> recipeMono = recipeReactiveRepository.findAll().next();
    	Mono<RecipeCommand> savedRecipeCommandMono = recipeMono.map(recipe -> {
    				RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);
    				recipeCommand.setDescription(NEW_DESCRIPTION);
    				return recipeCommand;
    			})
    			.flatMap(recipeCommand -> recipeService.saveRecipeCommand(recipeCommand));
    	//when
    	StepVerifier.create(Mono.zip(recipeMono, savedRecipeCommandMono))
    			.expectSubscription()
    			//then
    			.assertNext(tuple -> {
    				Recipe recipe = tuple.getT1();
    				RecipeCommand savedRecipeCommand = tuple.getT2();
    				assertEquals(NEW_DESCRIPTION, savedRecipeCommand.getDescription());
    		        assertEquals(recipe.getId(), savedRecipeCommand.getId());
    		        assertEquals(recipe.getCategories().size(), savedRecipeCommand.getCategories().size());
    		        assertEquals(recipe.getIngredients().size(), savedRecipeCommand.getIngredients().size());
    			})
    			.verifyComplete();
    }
}
