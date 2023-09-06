package com.mlorenzo.spring5reactivemongorecipeapp.services;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface ImageService {
    Mono<Void> saveImageFile(String recipeId, FilePart file);
    Mono<byte[]> getImage(String recipeId);
}
