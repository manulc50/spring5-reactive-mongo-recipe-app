package com.mlorenzo.spring5reactivemongorecipeapp.services;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientCommandToIngredient;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientToIngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.UnitOfMeasureCommandToUnitOfMeasure;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Ingredient;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class IngredientServiceImplTest {
    
	@Mock
	RecipeReactiveRepository recipeReactiveRepository;
    
    @Mock
    IngredientToIngredientCommand ingrToIngrCommandConverter;
	
	@Mock
    IngredientCommandToIngredient ingrCommandToIngrConverter;
	
	@Mock
    UnitOfMeasureCommandToUnitOfMeasure uomCommandToUomConverter;

    IngredientService ingredientService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ingredientService = new IngredientServiceImpl(recipeReactiveRepository, ingrToIngrCommandConverter, ingrCommandToIngrConverter, uomCommandToUomConverter);
    }
    
    @Test
    public void findByRecipeIdAndReceipeIdHappyPath() throws Exception {
        //given
    	Recipe recipe = new Recipe();
        recipe.setId("1");
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId("1");;
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId("2");
        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId("3");
        recipe.getIngredients().add(ingredient1);
        recipe.getIngredients().add(ingredient2);
        recipe.getIngredients().add(ingredient3);
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(ingredient3.getId());
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(ingrToIngrCommandConverter.convert(any(Ingredient.class))).thenReturn(ingredientCommand);
        //when
        StepVerifier.create(ingredientService.findByRecipeIdAndIngredientId("1", "3"))
        	//then 
        	.assertNext(command -> assertEquals("3", command.getId()))
        	.verifyComplete();
        verify(recipeReactiveRepository, times(1)).findById(anyString());
    }
    
    @Test
    public void testSaveRecipeCommand() throws Exception {
        //given
    	IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");
        Recipe savedRecipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(command.getId());
        savedRecipe.getIngredients().add(ingredient);
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(new Recipe()));
        when(recipeReactiveRepository.save(any(Recipe.class))).thenReturn(Mono.just(savedRecipe));
        when(ingrCommandToIngrConverter.convert(any(IngredientCommand.class))).thenReturn(ingredient);
        when(ingrToIngrCommandConverter.convert(any(Ingredient.class))).thenReturn(command);
        //when
        StepVerifier.create(ingredientService.saveIngredientCommand(command))
        	//then 
    		.assertNext(savedCommand -> assertEquals("3", savedCommand.getId()))
    		.verifyComplete();
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
    }
    
    @Test
    public void testDeleteById() throws Exception {
        //given
    	String idToDelete = "2";
        String idRecipe = "3";
        Recipe recipe = new Recipe();
        recipe.setId(idRecipe);
        Ingredient ingredient = new Ingredient();
        ingredient.setId(idToDelete);
        recipe.getIngredients().add(ingredient);
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeReactiveRepository.save(any(Recipe.class))).thenReturn(Mono.just(recipe));
        //when
        StepVerifier.create(ingredientService.deleteById(idRecipe, idToDelete))
        //then
        	.verifyComplete();
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
    }
}