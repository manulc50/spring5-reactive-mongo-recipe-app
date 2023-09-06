package com.mlorenzo.spring5reactivemongorecipeapp.commands;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import com.mlorenzo.spring5reactivemongorecipeapp.domain.Difficulty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RecipeCommand {
    private String id;

    // Nota: Los mensajes de validación del archivo de propiedades "messages.properties" tienen preferencia sobre aquellos que se
    // pongan en estas anotaciones de validación mediante el atributo "message"
    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255)
    private String description;

    @Min(1)
    @Max(999)
    private Integer prepTime;

    @Min(1)
    @Max(999)
    private Integer cookTime;

    @Min(1)
    @Max(100)
    private Integer servings;
    
    private String source;

    @URL
    private String url;

    @NotBlank
    private String directions;

    // En este caso usamos una colección de tipo List en vez de Set para poder realizar la conexión con la plantilla "recipeForm"
    private List<IngredientCommand> ingredients = new ArrayList<>();
    private byte[] image;
    private Difficulty difficulty;
    private String notes;
    private Set<CategoryCommand> categories = new HashSet<>();
}
