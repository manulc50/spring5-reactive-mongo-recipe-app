package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.mlorenzo.spring5reactivemongorecipeapp.services.CategoryService;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.CategoryCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.RecipeCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RecipeController {    
    private final RecipeService recipeService;
    private final CategoryService categoryService;
    private WebDataBinder webDataBinder;
    
    // Para poder manejar errores de validación en lugar de usar la anotación @Valid junto con BindingResult porque parece ser que dicha anotación junto con BindingResult no funciona correctamente con WebFlux
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
    	this.webDataBinder = webDataBinder;
    }
    
    @ModelAttribute("categorySet")
	public Flux<CategoryCommand> getAllCategories(){
		return categoryService.getCategories();
	}

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", Mono.just(new RecipeCommand()));
        return "recipe/recipeForm";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return "recipe/recipeForm";
    }

    // Parece ser que, usando WebFlux, no funciona correctamente la anotación @Valid junto con BindingResult
    // Como solución, usamos WebDataBinder para crear el BindingResult manualmente y así porder realizar la validación
    @PostMapping("recipe")
    public Mono<String> saveOrUpdate(@ModelAttribute("recipe") RecipeCommand command){
    	webDataBinder.validate();
    	BindingResult bindingResult = webDataBinder.getBindingResult();
        if(bindingResult.hasErrors()) {
        	if(log.isDebugEnabled())
	            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return Mono.just("recipe/recipeForm");
        }
        return recipeService.saveRecipeCommand(command)
        		.map(savedCommand -> "redirect:/recipe/" + savedCommand.getId() + "/show");
    }

    @GetMapping("recipe/{id}/delete")
    public Mono<String> deleteById(@PathVariable String id){
        log.debug("Deleting id: " + id);
        return recipeService.deleteById(id)
        	.then(Mono.just("redirect:/"));
    }

}
