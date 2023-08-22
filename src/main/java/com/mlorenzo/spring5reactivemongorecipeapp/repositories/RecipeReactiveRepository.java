package com.mlorenzo.spring5reactivemongorecipeapp.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;

public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String>{

}
