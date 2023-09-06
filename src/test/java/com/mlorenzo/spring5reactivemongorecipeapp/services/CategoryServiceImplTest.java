package com.mlorenzo.spring5reactivemongorecipeapp.services;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.mlorenzo.spring5reactivemongorecipeapp.converters.CategoryToCategoryCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Category;
import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.CategoryReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

//Anotación para poder usar Mockito en esta clase de pruebas
@RunWith(MockitoJUnitRunner.class) // Otra opción a esta anotación es usar la expresión o línea "MockitoAnnotations.initMocks(this);" en el método "setUp"
public class CategoryServiceImplTest {
	
	@Mock
	CategoryReactiveRepository categoryRepository;
	
	CategoryToCategoryCommand categoryToCategoryCommand = new CategoryToCategoryCommand();
	CategoryService service;
	
	@Before
	public void setUp() throws Exception {
		service = new CategoryServiceImpl(categoryRepository,categoryToCategoryCommand);
	}
	
	@Test
	public void getCategoriesTest() {
		//given
		Category cat1 = new Category();
		cat1.setId("1");
		Category cat2 = new Category();
		cat2.setId("2");
		when(categoryRepository.findAll()).thenReturn(Flux.just(cat1, cat2));
		//when
		StepVerifier.create(service.getCategories())
			//then
			.expectSubscription()
			.expectNextCount(2)
			.verifyComplete();
		// Si no se indica el número de llamadas en el método "times", por defecto es 1
		verify(categoryRepository).findAll();
	}
	
	@Test
	public void getCategoryByIdTest() throws Exception {
		String categoryId = "1";
        Category categoryReturned = new Category();
        categoryReturned.setId(categoryId);
        when(categoryRepository.findById(anyString())).thenReturn(Mono.just(categoryReturned));
        StepVerifier.create(service.getCategoryById(categoryId))
        	.expectSubscription()
        	.assertNext(categoryCommand -> assertNotNull("Null recipe returned", categoryCommand))
        	.expectComplete()
        	.verify();
        // Si no se indica el número de llamadas en el método "times", por defecto es 1
        verify(categoryRepository, times(1)).findById(anyString());
        verify(categoryRepository, never()).findAll();
    }
	
	@Test
	public void getCategoryByIdNotFoundTest() {
    	when(categoryRepository.findById(anyString())).thenReturn(Mono.empty());
    	//should go boom
    	StepVerifier.create(service.getCategoryById("1"))
    		.expectSubscription()
    		.expectError(NotFoundException.class)
    		.verify();
    	// Si no se indica el número de llamadas en el método "times", por defecto es 1
        verify(categoryRepository, times(1)).findById(anyString());
    }

}
