package com.mlorenzo.spring5reactivemongorecipeapp.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Ingredient {
	// Como esta clase no está anotada con @Document de MongoDB, si usamos la anotación @Id sobre esta propiedad, MongoDB no va a crear ids de manera automática para esta propiedad. Por esta razón, usamos la expresión "UUID.randomUUID().toString()" para generar valores únicos de ids de manera aleatoria
    private String id = UUID.randomUUID().toString();
    private String description;
    private BigDecimal amount;
    private UnitOfMeasure uom;

    public Ingredient() {

    }

    public Ingredient(String description, BigDecimal amount, UnitOfMeasure uom) {
        this.description = description;
        this.amount = amount;
        this.uom = uom;
    }

    public Ingredient(String description, BigDecimal amount, UnitOfMeasure uom, Recipe recipe) {
        this.description = description;
        this.amount = amount;
        this.uom = uom;
    }
}
