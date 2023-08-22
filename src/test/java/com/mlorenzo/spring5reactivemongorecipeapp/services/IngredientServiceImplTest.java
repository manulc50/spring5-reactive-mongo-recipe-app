package com.mlorenzo.spring5reactivemongorecipeapp.services;

import reactor.core.publisher.Mono;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientCommandToIngredient;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.IngredientToIngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.UnitOfMeasureCommandToUnitOfMeasure;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Ingredient;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.UnitOfMeasureReactiveRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class IngredientServiceImplTest {
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    
    @Mock
    RecipeReactiveRepository recipeReactiveRepository;
    
    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    IngredientService ingredientService;

    //init converters
    public IngredientServiceImplTest() {
        this.ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        this.ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand, ingredientCommandToIngredient,
                recipeReactiveRepository, unitOfMeasureReactiveRepository);
    }

    @Test
    public void findByRecipeIdAndId() throws Exception {
    }
    
    @Test
    public void findByRecipeIdAndReceipeIdHappyPath() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId("1");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId("1");
        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId("3");
        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);
        Mono<Recipe> recipeMono = Mono.just(recipe);
        when(recipeReactiveRepository.findById(anyString())).thenReturn(recipeMono);
        //when
        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1", "3").block();
        //then 
        assertEquals("3", ingredientCommand.getId());
        verify(recipeReactiveRepository, times(1)).findById(anyString());
    }
    
    @Test
    public void testSaveRecipeCommand() throws Exception {
        //given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");
        Mono<Recipe> recipeMono = Mono.just(new Recipe());
        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(new Ingredient());
        savedRecipe.getIngredients().iterator().next().setId("3");
        when(recipeReactiveRepository.findById(anyString())).thenReturn(recipeMono);
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just(savedRecipe));
        //when
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();
        //then
        assertEquals("3", savedCommand.getId());
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));

    }
    
    @Test
    public void testDeleteById() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");
        recipe.addIngredient(ingredient);
        Recipe recipeUpdated = new Recipe();
        recipeUpdated.setId("1");
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeReactiveRepository.save(any(Recipe.class))).thenReturn(Mono.just(recipeUpdated));
        //when
        ingredientService.deleteById("1", "3");
        //then
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
    }
}