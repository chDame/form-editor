package com.cda.form.model;

import java.io.Serializable;

public class FormData  implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -7792109320135994863L;
	
	private String name;
	
	private String type;
	
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
