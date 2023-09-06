package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.UnitOfMeasure;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {
	
	@Autowired
	UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
	
	@Test
	public void testUOMSave() throws Exception{
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setDescription("Pinch");
		Mono<Long> countMono = unitOfMeasureReactiveRepository.deleteAll()
				.then(unitOfMeasureReactiveRepository.save(uom))
				.then(unitOfMeasureReactiveRepository.count());
		StepVerifier.create(countMono)
			.expectNext(1L)
			.verifyComplete();
	}
	
	@Test
	public void testfindByDescription() throws Exception{
		String description = "Tablespoon";
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setDescription(description);
		Mono<UnitOfMeasure> uomMono = unitOfMeasureReactiveRepository.deleteAll()
				.then(unitOfMeasureReactiveRepository.save(uom))
				.then(unitOfMeasureReactiveRepository.findByDescription("Tablespoon"));
		StepVerifier.create(uomMono)
			.expectSubscription()
			.assertNext(fetchedUOM -> assertNotNull(fetchedUOM.getId()))
			.verifyComplete();
	}

}
