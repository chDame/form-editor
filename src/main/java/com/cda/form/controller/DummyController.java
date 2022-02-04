package com.cda.form.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dummy")
@CrossOrigin(origins = "*")
public class DummyController extends AbstractController {


	private final Logger logger = LoggerFactory.getLogger(FormsController.class);
	
	@GetMapping(value = "/1/{name}")
	@ResponseBody
    public Map<String, String> dummy1(@PathVariable String name)  {
		Map<String, String> data = new HashMap<>();
		data.put("value", name);
		return data;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
