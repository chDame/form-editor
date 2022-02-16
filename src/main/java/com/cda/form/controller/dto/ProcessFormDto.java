package com.cda.form.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessFormDto {

	private String activityId;
	private ProcessElementType type;
	private String formKey;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private boolean formExists;
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public ProcessElementType getType() {
		return type;
	}
	public void setType(ProcessElementType type) {
		this.type = type;
	}
	public String getFormKey() {
		return formKey;
	}
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}
	public boolean isFormExists() {
		return formExists;
	}
	public void setFormExists(boolean formExists) {
		this.formExists = formExists;
	}
}
