package com.cda.form.model.converter;

import java.io.IOException;
import java.util.List;

import javax.persistence.AttributeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cda.form.model.FormData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FormDataConverter implements AttributeConverter<List<FormData>, String> {
	
	private ObjectMapper objectMapper = new ObjectMapper();

	private final Logger logger = LoggerFactory.getLogger(FormDataConverter.class);
	
    @Override
    public String convertToDatabaseColumn(List<FormData> formData) {

        String formDataJson = null;
        try {
        	formDataJson = objectMapper.writeValueAsString(formData);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return formDataJson;
    }

    @Override
    public List<FormData> convertToEntityAttribute(String formDataJSON) {

    	List<FormData> customerInfo = null;
        try {
            customerInfo = objectMapper.readValue(formDataJSON, new TypeReference<List<FormData>>() {});
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }

        return customerInfo;
    }

}