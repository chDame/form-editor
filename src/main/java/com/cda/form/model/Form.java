package com.cda.form.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.cda.form.model.converter.FormContentConverter;
import com.cda.form.model.converter.FormDataConverter;

@Entity
public class Form {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private Date creation;
	
	@Lob
    @Convert(converter = FormContentConverter.class)
	private FormContent content;

	@Lob
    @Convert(converter = FormDataConverter.class)
	private List<FormData> data;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public FormContent getContent() {
		return content;
	}

	public void setContent(FormContent content) {
		this.content = content;
	}

	public List<FormData> getData() {
		return data;
	}

	public void setData(List<FormData> data) {
		this.data = data;
	}
	
	
}
