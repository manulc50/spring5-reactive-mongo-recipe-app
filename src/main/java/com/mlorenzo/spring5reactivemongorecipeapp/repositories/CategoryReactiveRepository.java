package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Category;

import reactor.core.publisher.Mono;

public interface CategoryReactiveRepository extends ReactiveMongoRepository<Category, String>{
	Mono<Category> findByDescription(String description);
}
