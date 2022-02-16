package com.cda.form.controller.admin;


import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cda.form.controller.AbstractController;
import com.cda.form.model.Form;
import com.cda.form.service.FormService;

@Controller
@RequestMapping("/edition/forms")
@CrossOrigin(origins = "*")
public class FormsEditionController extends AbstractController {


	private final Logger logger = LoggerFactory.getLogger(FormsEditionController.class);
	
	@Autowired
	private FormService formService;

	@PostMapping
	public ResponseEntity<Form> create(@RequestBody Form form) {
		form = formService.save(form);
		return new ResponseEntity<>(form, HttpStatus.CREATED);
	}
	
	@GetMapping(value = "/names")
	@ResponseBody
    public List<String> formNames()  {
		return formService.findNames();
	}
	
	@GetMapping
	@ResponseBody
    public List<Form> list(@RequestParam(required = false) String name)  {
		if (StringUtils.isNotBlank(name)) {
			Form form = formService.findByName(name);
			if (form==null) {
				return Collections.emptyList();
			}
			return Collections.singletonList(form);
		}
		return formService.findAll();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
