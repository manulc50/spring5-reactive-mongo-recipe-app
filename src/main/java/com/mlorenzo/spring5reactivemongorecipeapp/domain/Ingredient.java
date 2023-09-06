package com.mlorenzo.spring5reactivemongorecipeapp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
@NoArgsConstructor
public class Ingredient {
	private String id;
    private String description;
    private BigDecimal amount;
    
    @DBRef
    private UnitOfMeasure uom;

    public Ingredient(String id, String description, BigDecimal amount, UnitOfMeasure uom) {
    	this.id = id;
        this.description = description;
        this.amount = amount;
        this.uom = uom;
    }

}
