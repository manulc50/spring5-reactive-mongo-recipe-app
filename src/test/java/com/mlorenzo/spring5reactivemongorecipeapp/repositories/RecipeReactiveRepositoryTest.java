package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryTest {
	
	@Autowired
	RecipeReactiveRepository recipeReactiveReporsitory;
	
	@Test
	public void testRecipeSave() throws Exception{
		Recipe recipe = new Recipe();
		recipe.setDescription("Yummy");
		Mono<Long> countMono = recipeReactiveReporsitory.deleteAll()
				.then(recipeReactiveReporsitory.save(recipe))
				.then(recipeReactiveReporsitory.count());
		StepVerifier.create(countMono)
			.expectSubscription()
			.expectNext(1L)
			.expectComplete()
			.verify();
	}
}
