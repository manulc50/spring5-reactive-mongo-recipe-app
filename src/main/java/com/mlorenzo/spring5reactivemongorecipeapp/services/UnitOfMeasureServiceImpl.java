package com.mlorenzo.spring5reactivemongorecipeapp.services;

import reactor.core.publisher.Flux;

import org.springframework.stereotype.Service;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.UnitOfMeasureCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.UnitOfMeasureReactiveRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepositoryReactive;
    private final UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

	@Override
	public Flux<UnitOfMeasureCommand> listAllUoms() {
		return unitOfMeasureRepositoryReactive.findAll()
				.map(unitOfMeasureToUnitOfMeasureCommand::convert);
	}
}
