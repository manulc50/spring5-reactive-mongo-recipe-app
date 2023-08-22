package com.mlorenzo.spring5reactivemongorecipeapp.services;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.UnitOfMeasureCommand;

import reactor.core.publisher.Flux;

public interface UnitOfMeasureService {
    Flux<UnitOfMeasureCommand> listAllUoms();
}
