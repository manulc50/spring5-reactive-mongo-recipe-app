package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryTest {
	
	@Autowired
	RecipeReactiveRepository recipeReactiveReporsitory;
	
	@Before
	public void setUp() throws Exception{
		recipeReactiveReporsitory.deleteAll().block();
	}
	
	@Test
	public void testRecipeSave() throws Exception{
		Recipe recipe = new Recipe();
		recipe.setDescription("Yummy");
		recipeReactiveReporsitory.save(recipe).block();
		Long count = recipeReactiveReporsitory.count().block();
		assertEquals(Long.valueOf(1L),count);
	}
}
