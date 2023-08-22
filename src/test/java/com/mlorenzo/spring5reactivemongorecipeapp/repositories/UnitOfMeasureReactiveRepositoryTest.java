package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.UnitOfMeasure;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {
	
	@Autowired
	UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
	
	@Before
	public void setUp() throws Exception{
		unitOfMeasureReactiveRepository.deleteAll().block();
	}
	
	@Test
	public void testUOMSave() throws Exception{
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setDescription("Pinch");
		unitOfMeasureReactiveRepository.save(uom).block();
		Long count = unitOfMeasureReactiveRepository.count().block();
		assertEquals(Long.valueOf(1L),count);
	}
	
	@Test
	public void testfindByDescription() throws Exception{
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setDescription("Tablespoon");
		unitOfMeasureReactiveRepository.save(uom).block();
		UnitOfMeasure fetchedUOM = unitOfMeasureReactiveRepository.findByDescription("Tablespoon").block();
		assertNotNull(fetchedUOM.getId());
	}

}
