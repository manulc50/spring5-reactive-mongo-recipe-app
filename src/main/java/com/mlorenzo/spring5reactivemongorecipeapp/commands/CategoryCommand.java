package com.mlorenzo.spring5reactivemongorecipeapp.commands;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.CategoryCommand;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

// Nota: Implementamos en esta clase los métodos "equals" y "hashCode" mediante Lombok para comparar las categorías de una receta
// y así poder marcar o desmarcar los checkboxes de las categorías en la plantilla "recipeForm"
@Setter
@Getter
@EqualsAndHashCode
public class CategoryCommand {
    private String id;
    
    // Excluimos esta propiedad de la implementación de los métodos "equals" y "hashCode" que realiza Lombok
    @EqualsAndHashCode.Exclude
    private String description;
}
