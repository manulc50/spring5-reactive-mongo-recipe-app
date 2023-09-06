package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Category;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryTest {
	
	@Autowired
	CategoryReactiveRepository categoryReactiveReporsitory;
	
	@Before
	public void setUp() throws Exception{
		categoryReactiveReporsitory.deleteAll().block();
	}
	
	@Test
	public void testCategorySave() throws Exception{
		Category category = new Category();
		category.setDescription("Spanish");
		Mono<Long> countMono = categoryReactiveReporsitory.save(category)
				.then(categoryReactiveReporsitory.count());
		StepVerifier.create(countMono)
			.expectNext(1L)
			.verifyComplete();
	}
	
	@Test
	public void testfindByDescription() throws Exception{
		String description = "Mexican";
		Category category = new Category();
		category.setDescription(description);
		Mono<Category> categoryMono = categoryReactiveReporsitory.save(category)
				.then(categoryReactiveReporsitory.findByDescription(description));
		StepVerifier.create(categoryMono)
			.assertNext(cat -> assertNotNull(cat.getId()))
			.verifyComplete();
	}

}
