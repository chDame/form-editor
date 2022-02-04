package com.cda.form.edition.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyTypeEnum {
	
	TEXT("text"),
	NUMBER("number"),
	BOOLEAN("boolean"),
	LIST("list"),
	BINDING("binding");
	
	private String value;
	
	private PropertyTypeEnum(String value) {
        this.value = value;
    }
	
	@JsonValue
    public String getValue() {
        return value;
    }
}
