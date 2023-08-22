package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Category;

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
		categoryReactiveReporsitory.save(category).block();
		Long count = categoryReactiveReporsitory.count().block();
		assertEquals(Long.valueOf(1L),count);
	}
	
	@Test
	public void testfindByDescription() throws Exception{
		Category category = new Category();
		category.setDescription("Mexican");
		categoryReactiveReporsitory.save(category).block();
		Category fetchedCat = categoryReactiveReporsitory.findByDescription("Mexican").block();
		assertNotNull(fetchedCat.getId());
	}

}
