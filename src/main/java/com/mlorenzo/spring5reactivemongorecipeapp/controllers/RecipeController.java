package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

@Slf4j
@Controller
public class RecipeController {
    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    
    private final RecipeService recipeService;
    
    private WebDataBinder webDataBinder;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    // Para poder manejar errores de validación en lugar de usar la anotación @Valid junto con BindingResult porque parece ser que dicha anotación junto con BindingResult no funciona correctamente con WebFlux
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
    	this.webDataBinder = webDataBinder;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findById(id));
        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());
        return "recipe/recipeform";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return RECIPE_RECIPEFORM_URL;
    }

    // Parece ser que, usando WebFlux, no funciona correctamente la anotación @Valid junto con BindingResult
    // Como solución, usamos WebDataBinder para crear el BindingResult manualmente y así porder realizar la validación
    @PostMapping("recipe")
    public String saveOrUpdate(@ModelAttribute("recipe") RecipeCommand command){
    	webDataBinder.validate();
    	BindingResult bindingResult = webDataBinder.getBindingResult();
        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return RECIPE_RECIPEFORM_URL;
        }
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command).block();
        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id){
        log.debug("Deleting id: " + id);
        recipeService.deleteById(id).block();
        return "redirect:/";
    }

    // "ModelAndView" no funciona en WebFlux porque es reactivo.Sólo funciona en Web no reactivo porque es específico de la API de Servlet
    // Por esta razón, cambiamos el tipo de dato devuelto por este método de "ModelAndView" a String
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(Exception exception,Model model){
        log.error("Handling not found exception");
        log.error(exception.getMessage());
        model.addAttribute("exception", exception);
        return "404error";
    }

}
