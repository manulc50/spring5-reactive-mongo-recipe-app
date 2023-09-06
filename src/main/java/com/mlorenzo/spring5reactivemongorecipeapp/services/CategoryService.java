package com.mlorenzo.spring5reactivemongorecipeapp.services;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.CategoryCommand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
	Flux<CategoryCommand> getCategories();
	Mono<CategoryCommand> getCategoryById(String id);
}
