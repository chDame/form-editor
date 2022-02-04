package com.cda.form.model;

import java.io.Serializable;

public class PropFn implements Serializable {

	/**
	 * Seriql version UID
	 */
	private static final long serialVersionUID = -1549504009519142769L;

	private boolean active;
	
	private String value;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
