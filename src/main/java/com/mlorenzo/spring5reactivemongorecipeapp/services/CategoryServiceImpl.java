package com.mlorenzo.spring5reactivemongorecipeapp.services;

import org.springframework.stereotype.Service;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.CategoryCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.CategoryToCategoryCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.CategoryReactiveRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{
	private final CategoryReactiveRepository categoryRepository;
	private final CategoryToCategoryCommand categoryToCategoryCommand;

	@Override
	public Flux<CategoryCommand> getCategories() {
		return categoryRepository.findAll()
				// Versión simplificada de la expresión "category -> categoryToCategoryCommand.convert(category)"
				.map(categoryToCategoryCommand::convert);
	}

	@Override
	public Mono<CategoryCommand> getCategoryById(String id) {
		return categoryRepository.findById(id)
				.switchIfEmpty(Mono.error(new NotFoundException("Category Not Found for id value: " + id)))
				// Versión simplificada de la expresión "category -> categoryToCategoryCommand.convert(category)"
				.map(categoryToCategoryCommand::convert);
	}
}
