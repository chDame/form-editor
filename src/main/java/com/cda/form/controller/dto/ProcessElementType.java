package com.cda.form.controller.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessElementType {

	START_EVENT("StartEvent"),
	USER_TASK("UserTask"),
	SERVICE_TASK("ServiceTask");
	
	private String value;
	
	private ProcessElementType(String value) {
        this.value = value;
    }
	
	@JsonValue
    public String getValue() {
        return value;
    }
	
}
