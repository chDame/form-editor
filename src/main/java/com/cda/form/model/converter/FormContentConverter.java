package com.cda.form.model.converter;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cda.form.model.FormContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FormContentConverter implements AttributeConverter<FormContent, String> {
	
	private ObjectMapper objectMapper = new ObjectMapper();

	private final Logger logger = LoggerFactory.getLogger(FormContentConverter.class);
	
    @Override
    public String convertToDatabaseColumn(FormContent formContent) {

        String contentJson = null;
        try {
        	contentJson = objectMapper.writeValueAsString(formContent);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return contentJson;
    }

    @Override
    public FormContent convertToEntityAttribute(String formContentJSON) {

    	FormContent formContent = null;
        try {
        	formContent = objectMapper.readValue(formContentJSON, FormContent.class);
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }

        return formContent;
    }

}