package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.bootstrap.RecipeBootstrap;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.UnitOfMeasure;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

	@Autowired
	UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
	
	@Autowired
    CategoryReactiveRepository categoryReactiveRepository;
    
    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @Before
    public void setUp() throws Exception {
    	recipeReactiveRepository.deleteAll().block();
    	unitOfMeasureReactiveRepository.deleteAll().block();
    	categoryReactiveRepository.deleteAll().block();
    	RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryReactiveRepository, recipeReactiveRepository, unitOfMeasureReactiveRepository);
    	recipeBootstrap.onApplicationEvent(null);
    }

    @Test
    public void findByDescription() throws Exception {
        Mono<UnitOfMeasure> uomMono = unitOfMeasureReactiveRepository.findByDescription("Teaspoon");
        assertEquals("Teaspoon", uomMono.block().getDescription());
    }

    @Test
    public void findByDescriptionCup() throws Exception {
        Mono<UnitOfMeasure> uomMono= unitOfMeasureReactiveRepository.findByDescription("Cup");
        assertEquals("Cup", uomMono.block().getDescription());
    }

}