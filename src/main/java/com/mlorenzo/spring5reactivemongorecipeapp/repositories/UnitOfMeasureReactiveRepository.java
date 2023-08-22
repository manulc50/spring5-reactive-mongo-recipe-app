package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.UnitOfMeasure;

import reactor.core.publisher.Mono;

public interface UnitOfMeasureReactiveRepository extends ReactiveMongoRepository<UnitOfMeasure, String>{
	Mono<UnitOfMeasure> findByDescription(String description);
}
