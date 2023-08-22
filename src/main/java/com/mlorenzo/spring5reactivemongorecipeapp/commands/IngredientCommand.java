package com.mlorenzo.spring5reactivemongorecipeapp.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class IngredientCommand {
    private String id;
    private String recipeId;
    
    @NotBlank
    private String description;
    
    @NotNull
    @Min(1)
    private BigDecimal amount;
    
    @NotNull
    private UnitOfMeasureCommand uom;
}