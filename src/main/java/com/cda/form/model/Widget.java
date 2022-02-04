package com.cda.form.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = ContentWidget.class, name = "contentWidget"),
		@Type(value = Widget.class, name = "widget") })
public class Widget implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1230086808926910826L;

	private String name;

	private String nature;

	private String display;

	private Boolean sizeable;

	private Map<String, Object> props;

	private Map<String, PropFn> propsFn;

	private Map<String, String> binding;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Boolean getSizeable() {
		return sizeable;
	}

	public void setSizeable(Boolean sizeable) {
		this.sizeable = sizeable;
	}
	
	public Map<String, Object> getProps() {
		return props;
	}

	public void setProps(Map<String, Object> props) {
		this.props = props;
	}

	public Map<String, PropFn> getPropsFn() {
		return propsFn;
	}

	public void setPropsFn(Map<String, PropFn> propsFn) {
		this.propsFn = propsFn;
	}

	public Map<String, String> getBinding() {
		return binding;
	}

	public void setBinding(Map<String, String> binding) {
		this.binding = binding;
	}
}
