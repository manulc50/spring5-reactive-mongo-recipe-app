package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.bootstrap.RecipeBootstrap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@Import(RecipeBootstrap.class)
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

	@Autowired
	UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
	
	@Autowired
    CategoryReactiveRepository categoryReactiveRepository;
    
    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @Test
    public void findByDescription() throws Exception {
        StepVerifier.create(unitOfMeasureReactiveRepository.findByDescription("Teaspoon"))
        	.expectSubscription()
        	.assertNext(uom -> assertEquals("Teaspoon", uom.getDescription()))
        	.verifyComplete();
    }

    @Test
    public void findByDescriptionCup() throws Exception {
        StepVerifier.create(unitOfMeasureReactiveRepository.findByDescription("Cup"))
    		.expectSubscription()
    		.assertNext(uom -> assertEquals("Cup", uom.getDescription()))
    		.expectComplete()
    		.verify();
    }

}