package com.cda.form.controller.admin;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cda.form.controller.AbstractController;
import com.cda.form.edition.model.WidgetDefinitionUtils;
import com.cda.form.exception.TechnicalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/edition")
@CrossOrigin(origins = "*")
public class WidgetEditionController extends AbstractController {


	private final Logger logger = LoggerFactory.getLogger(WidgetEditionController.class);
	
	private ObjectMapper objMapper = new ObjectMapper();
	
	@GetMapping(value = "/components.js", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
    public String components()  {
		try {
			return "window.widgets = { containers:"+
					objMapper.writeValueAsString(WidgetDefinitionUtils.getContainers())+
					", widgets:"+objMapper.writeValueAsString(WidgetDefinitionUtils.getWidgets())+
					"}";
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
	}
	
	

	@Override
	public Logger getLogger() {
		return logger;
	}
}
