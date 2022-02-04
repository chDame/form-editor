package com.cda.form.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cda.form.exception.UnexpectedOperationException;
import com.cda.form.model.ContentWidget;
import com.cda.form.model.Form;
import com.cda.form.model.FormData;
import com.cda.form.model.PropFn;
import com.cda.form.model.Widget;
import com.cda.form.repo.FormRepository;

@Service
public class FormService {

	@Autowired
	private FormRepository formRepository;
	
	public Form save(Form form) {
		if (StringUtils.isBlank(form.getName())) {
			throw new UnexpectedOperationException("Form should have a name.");
		}
		Form check = findByName(form.getName());
		if (check!=null && !check.getId().equals(form.getId())) {
			throw new UnexpectedOperationException("Another form exists with that name. You should rename it first.");
		}
		return formRepository.save(form);
	}
	
	
	public Form findById(Long id) {
		return formRepository.findById(id).orElse(null);
	}
	
	public Form findByName(String name) {
		return formRepository.findByName(name);
	}
	
	public List<String> findNames() {
		return formRepository.findNames();
	}
	
	public List<Form> findAll() {
		return formRepository.findAll();
	}


}
