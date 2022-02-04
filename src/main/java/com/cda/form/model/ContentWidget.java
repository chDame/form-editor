package com.cda.form.model;

import java.util.List;

public class ContentWidget extends Widget {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 5475662556784850703L;
	
	private List<Widget> content;

	public List<Widget> getContent() {
		return content;
	}

	public void setContent(List<Widget> content) {
		this.content = content;
	}
}
