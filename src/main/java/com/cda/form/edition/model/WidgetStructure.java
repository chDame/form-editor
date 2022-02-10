package com.cda.form.edition.model;

import java.util.List;

public class WidgetStructure {
	private String type;
	private String nature;
	private String display;
	private boolean sizeable;
	private String icon;
	private String template;
	private String methods;
	private List<PropertyDefinition> propsDef;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public boolean isSizeable() {
		return sizeable;
	}
	public void setSizeable(boolean sizeable) {
		this.sizeable = sizeable;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public List<PropertyDefinition> getPropsDef() {
		return propsDef;
	}
	public void setPropsDef(List<PropertyDefinition> propsDef) {
		this.propsDef = propsDef;
	}
	public String getMethods() {
		return methods;
	}
	public void setMethods(String methods) {
		this.methods = methods;
	}
	
}
