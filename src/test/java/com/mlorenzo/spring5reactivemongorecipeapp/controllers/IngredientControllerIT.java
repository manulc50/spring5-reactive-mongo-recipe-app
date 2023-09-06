package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.IngredientCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.commands.UnitOfMeasureCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.services.IngredientService;
import com.mlorenzo.spring5reactivemongorecipeapp.services.UnitOfMeasureService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
// Nota: Por defecto, esta anotación hace que también se procesen las plantillas(En nuestro caso, plantillas Thymeleaf)
@WebFluxTest(controllers = IngredientController.class)
public class IngredientControllerIT {
	
	@Autowired
	WebTestClient webTestClient;

    @MockBean
    IngredientService ingredientService;

    @MockBean
    UnitOfMeasureService unitOfMeasureService;

    @Test
    public void testListIngredients() throws Exception {
    	//given
        when(ingredientService.getIngredients(anyString())).thenReturn(Flux.empty());
        //when
        webTestClient.get().uri("/recipe/1/ingredients")
        	.exchange()
        	//then
        	.expectStatus().isOk();
        // Si no se indica el número de llamadas en el método "times", por defecto es 1
        verify(ingredientService, times(1)).getIngredients(anyString());
    }

    @Test
    public void testShowIngredient() throws Exception {
    	//given
        IngredientCommand ingredientCommand = new IngredientCommand();
        UnitOfMeasureCommand uomCommand = new UnitOfMeasureCommand();
        ingredientCommand.setUom(uomCommand);
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(ingredientCommand));
        //when
        webTestClient.get().uri("/recipe/1/ingredient/2/show")
        	.exchange()
			//then
			.expectStatus().isOk();
        // Si no se indica el número de llamadas en el método "times", por defecto es 1
        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId(anyString(), anyString());
    }

    @Test
    public void testNewIngredientForm() throws Exception {
    	//given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId("1");
        when(ingredientService.createNewIngredient(anyString())).thenReturn(Mono.just(ingredientCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.empty());
        //when
        webTestClient.get().uri("/recipe/1/ingredient/new")
	        .exchange()
			//then
			.expectStatus().isOk();
        verify(ingredientService, times(1)).createNewIngredient(anyString());
    }

    @Test
    public void testUpdateIngredientForm() throws Exception {
    	//given
        IngredientCommand ingredientCommand = new IngredientCommand();
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(ingredientCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.empty());
        //when
        webTestClient.get().uri("/recipe/1/ingredient/2/update")
			.exchange()
			//then
			.expectStatus().isOk();
        // Si no se indica el número de llamadas en el método "times", por defecto es 1
        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId(anyString(), anyString());
        verify(unitOfMeasureService, times(1)).listAllUoms();
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
    	//given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");
        when(ingredientService.saveIngredientCommand(any())).thenReturn(Mono.just(command));
        //when
        webTestClient.post().uri("/recipe/2/ingredient")
        		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        		.body(BodyInserters.fromFormData("id", "")
                        .with("description", "some string"))
        		.exchange()
    			//then
    			.expectStatus().is3xxRedirection();
        // Si no se indica el número de llamadas en el método "times", por defecto es 1
        verify(ingredientService).saveIngredientCommand(any());
    }

    @Test
    public void testDeleteIngredient() throws Exception {
    	//given
    	when(ingredientService.deleteById(anyString(), anyString())).thenReturn(Mono.empty());
    	//when
    	webTestClient.get().uri("/recipe/2/ingredient/3/delete")
    		.exchange()
    		//then
    		.expectStatus().is3xxRedirection();
        // Si no se indica el número de llamadas en el método "times", por defecto es 1
        verify(ingredientService).deleteById(anyString(), anyString());
    }
}