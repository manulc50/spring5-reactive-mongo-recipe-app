package com.mlorenzo.spring5reactivemongorecipeapp.controllers;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mlorenzo.spring5reactivemongorecipeapp.services.ImageService;
import com.mlorenzo.spring5reactivemongorecipeapp.services.RecipeService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
public class ImageController {
    private final ImageService imageService;
    private final RecipeService recipeService;

    @GetMapping("recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return "recipe/imageUploadForm";
    }

    @PostMapping("recipe/{id}/image")
    public Mono<String> handleImagePost(@PathVariable String id, @RequestPart("imagefile") FilePart file){
        return imageService.saveImageFile(id, file)
        		.thenReturn("redirect:/recipe/" + id + "/show");
    }

    @ResponseBody
    @GetMapping("recipe/{id}/recipeimage")
    public Mono<Void> renderImageFromDB(@PathVariable String id, ServerHttpResponse response) {
    	return response.writeWith(imageService.getImage(id)
    			.map(arrayBytes -> {
    				DataBufferFactory factory = response.bufferFactory();
    				return factory.wrap(arrayBytes);
    			}));
    }
}
