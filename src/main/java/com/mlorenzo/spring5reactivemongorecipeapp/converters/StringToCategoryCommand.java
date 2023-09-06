package com.mlorenzo.spring5reactivemongorecipeapp.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.CategoryCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class StringToCategoryCommand implements Converter<String, CategoryCommand>{

	@Override
	public CategoryCommand convert(String id) {
		CategoryCommand categoryCommand = new CategoryCommand();
		categoryCommand.setId(id);
		return categoryCommand;
	}

}
