package com.mlorenzo.spring5reactivemongorecipeapp.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Setter
@Document(collection = "categories")
public class Category {
	
    @Id
    private String id;
    
    private String description;
    private Set<Recipe> recipes;
}
