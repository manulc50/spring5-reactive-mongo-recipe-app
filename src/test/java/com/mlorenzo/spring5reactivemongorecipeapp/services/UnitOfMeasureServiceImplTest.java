package com.mlorenzo.spring5reactivemongorecipeapp.services;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mlorenzo.spring5reactivemongorecipeapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.UnitOfMeasure;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.UnitOfMeasureReactiveRepository;

import static org.mockito.Mockito.*;

public class UnitOfMeasureServiceImplTest {
    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand = new UnitOfMeasureToUnitOfMeasureCommand();
    UnitOfMeasureService service;
    
    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new UnitOfMeasureServiceImpl(unitOfMeasureReactiveRepository, unitOfMeasureToUnitOfMeasureCommand);
    }
    
    @Test
    public void listAllUoms() throws Exception {
        //given
        UnitOfMeasure uom1 = new UnitOfMeasure();
        uom1.setId("1");
        UnitOfMeasure uom2 = new UnitOfMeasure();
        uom2.setId("2");
        when(unitOfMeasureReactiveRepository.findAll()).thenReturn(Flux.just(uom1,uom2));
        //when
        StepVerifier.create(service.listAllUoms())
        	//then
        	.expectNextCount(2)
        	.verifyComplete();
        verify(unitOfMeasureReactiveRepository, times(1)).findAll();
    }

}