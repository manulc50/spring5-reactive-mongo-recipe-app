package com.mlorenzo.spring5reactivemongorecipeapp.services;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.mlorenzo.spring5reactivemongorecipeapp.exceptions.NotFoundException;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Recipe;
import com.mlorenzo.spring5reactivemongorecipeapp.repositories.RecipeReactiveRepository;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final RecipeReactiveRepository recipeReactiveRepository;

	@Override
	public Mono<Void> saveImageFile(String recipeId, FilePart file) {
		return Mono.zip(recipeReactiveRepository.findById(recipeId),
				DataBufferUtils.join(file.content()).map(dataBuffer -> dataBuffer.asByteBuffer().array()))
			.flatMap(tuple -> {
				Recipe recipe = tuple.getT1();
				byte[] fileBytes = tuple.getT2();
				recipe.setImage(fileBytes);
	            return recipeReactiveRepository.save(recipe);
			})
			.then();
	}
	
	@Override
	public Mono<byte[]> getImage(String recipeId) {
		return recipeReactiveRepository.findById(recipeId)
				.switchIfEmpty(Mono.error(new NotFoundException("Recipe Not Found for id value: " + recipeId)))
				.flatMap(recipe -> recipe.getImage() != null ? Mono.just(recipe.getImage()) : Mono.empty());
	}
}
