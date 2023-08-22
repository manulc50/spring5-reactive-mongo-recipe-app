package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.UnitOfMeasureCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.services.IngredientService;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;
import com.mlorenzo.spring5reactivemongorecipeapp.services.UnitOfMeasureService;

@Slf4j
@Controller
public class IngredientController {
    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final UnitOfMeasureService unitOfMeasureService;
    
    private WebDataBinder webDataBinder;

    public IngredientController(IngredientService ingredientService, RecipeService recipeService, UnitOfMeasureService unitOfMeasureService) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.unitOfMeasureService = unitOfMeasureService;
    }
    
    // Para poder manejar errores de validación en lugar de usar la anotación @Valid junto con BindingResult porque parece ser que dicha anotación junto con BindingResult no funciona correctamente con WebFlux
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
    	this.webDataBinder = webDataBinder;
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);
        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));
        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newRecipe(@PathVariable String recipeId, Model model){
        //make sure we have a good id value
        RecipeCommand recipeCommand = recipeService.findCommandById(recipeId).block();
        //todo raise exception if null
        //need to return back parent id for hidden form property
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeId);
        model.addAttribute("ingredient", ingredientCommand);
        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());
        // Comentamos esta línea porque ahora el atributo "uomList" de la vista es común a todos los métodos handler de este controlador 
        //model.addAttribute("uomList",  unitOfMeasureService.listAllUomsReactive());
        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
    	model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
    	// Comentamos esta línea porque ahora el atributo "uomList" de la vista es común a todos los métodos handler de este controlador 
        //model.addAttribute("uomList", unitOfMeasureService.listAllUomsReactive());
        return "recipe/ingredient/ingredientform";
    }

    // Parece ser que, usando WebFlux, no funciona correctamente la anotación @Valid junto con BindingResult
    // Como solución, usamos WebDataBinder para crear el BindingResult manualmente y así porder realizar la validación
    @PostMapping("recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute("ingredient") IngredientCommand command,Model model){
    	webDataBinder.validate();
    	BindingResult bindingResult = webDataBinder.getBindingResult();
    	if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            // Comentamos esta línea porque ahora el atributo "uomList" de la vista es común a todos los métodos handler de este controlador 
            //model.addAttribute("uomList", unitOfMeasureService.listAllUomsReactive());
            return "recipe/ingredient/ingredientform";
        }
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();
        log.debug("saved ingredient id:" + savedCommand.getId());
        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public String deleteIngredient(@PathVariable String recipeId,
                                   @PathVariable String id){
        log.debug("deleting ingredient id:" + id);
        ingredientService.deleteById(recipeId, id).block();
        return "redirect:/recipe/" + recipeId + "/ingredients";
    }
    
    // Como el atributo "uomList" se utiliza en varios métodos handler("newRecipe", "updateRecipeIngredient" y "saveOrUpdate"), podemos refactorizar el código creando un atributo de la vista común a todos ellos
    // Con esta anotación creamos el atributo de la vista "uomList" que es común a todos las vistas devueltas por los métodos handler indicados en este controlador
    @ModelAttribute("uomList") // En este caso, el flujo reactivo Fluz con elementos de tipo UnitOfMeasureCommand devuelto por este método se asigna al atributo de la vista común "uomList"
    public Flux<UnitOfMeasureCommand> populateUomList(){
    	return unitOfMeasureService.listAllUoms();
    }
}
