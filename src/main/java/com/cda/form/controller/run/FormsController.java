package com.cda.form.controller.run;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cda.form.compil.utils.FormUtils;
import com.cda.form.controller.AbstractController;
import com.cda.form.exception.TechnicalException;
import com.cda.form.model.Form;
import com.cda.form.service.FormService;

@Controller
@RequestMapping("/forms")
@CrossOrigin(origins = "*")
public class FormsController extends AbstractController {


	private final Logger logger = LoggerFactory.getLogger(FormsController.class);
	
	@Autowired
	private FormService formService;

	
	@GetMapping(value = "/{name}", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
    public String formByName(@PathVariable String name)  {
		Form form = formService.findByName(name);
		if (form==null) {
			return null;
		}
		try {
			return FormUtils.buildFullApplication(form);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
