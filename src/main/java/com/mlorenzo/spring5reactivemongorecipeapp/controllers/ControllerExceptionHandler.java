package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

	// "ModelAndView" no funciona en WebFlux porque es reactivo.Sólo funciona en Web no reactivo porque es específico de la API de Servlet
    // Por esta razón, cambiamos el tipo de dato devuelto por este método de "ModelAndView" a String
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public String handleNumberFormat(Exception exception,Model model){
        log.error("Handling Number Format Exception");
        log.error(exception.getMessage());
        model.addAttribute("exception", exception);
        return "400error";
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
