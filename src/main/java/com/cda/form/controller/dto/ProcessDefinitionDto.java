package com.cda.form.controller.dto;

public class ProcessDefinitionDto {
    private String id;
    private String deploymentId;
    private String key;
    private int version;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDeploymentId() {
        return deploymentId;
    }
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
    
}
