package com.cda.form.controller.run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cda.form.compil.utils.FormUtils;
import com.cda.form.controller.AbstractController;
import com.cda.form.controller.dto.ProcessDefinitionDto;
import com.cda.form.controller.dto.ProcessInstanceDto;
import com.cda.form.exception.TechnicalException;
import com.cda.form.model.Form;


@RestController
@RequestMapping("/bpm")
public class ProcessController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(ProcessController.class);

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private SimpMessagingTemplate socketTemplate;

	@Autowired
	private com.cda.form.service.FormService cdaFormService;

	@Autowired
	private FormService formService;

	@RequestMapping(value = "/process", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public List<ProcessDefinitionDto> getActiveProcessList() {
		socketTemplate.convertAndSend("/message",  "shield");

		List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery().active().list();
		List<ProcessDefinitionDto> result = new ArrayList<>();
		for (ProcessDefinition pd: processList ) {
			ProcessDefinitionDto dto= new ProcessDefinitionDto();
			BeanUtils.copyProperties(pd , dto);
			result.add(dto);
		}
		logger.info("Process list count is {}", processList.size());
		return result;

	}


	@RequestMapping(value = "/process/{processDefinitionId}/execute", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public ProcessInstanceDto startProcessInstanceByKey(@PathVariable String processDefinitionId, @RequestBody Map<String, Object> variables){
		//identityService.setAuthenticatedUserId(SecurityUtils.getConnectedUser().getUsername());
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
		logger.info("New process instance initiated. Process instance id is {}", processInstance.getId());

		ProcessInstanceDto dto= new ProcessInstanceDto();
		BeanUtils.copyProperties(processInstance, dto);
		return dto;
	}

	@RequestMapping(value = "/task/{id}/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void executeTask(@PathVariable String id, @RequestBody Map<String, Object> variables){
		//identityService.setAuthenticatedUserId(SecurityUtils.getConnectedUser().getUsername());
		taskService.complete(id, variables);
	}

	@RequestMapping(value = "/task", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public String taskForm(@RequestParam String taskId){
		String formKey = formService.getTaskFormData(taskId).getFormKey();
		if (formKey!=null) {
			Form form = cdaFormService.findByName(formKey);
			if (form!=null) {
				try {
					return FormUtils.buildFullApplication(form);
				} catch (IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
		return null;
	}


	@RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public VariableMap taskVariables(@PathVariable String taskId){
		return taskService.getVariablesTyped(taskId);
	}

	@RequestMapping(value = "/process/{processDefinitionId}/start", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public String startForm(@PathVariable String processDefinitionId){
		BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
		ModelElementType startType = modelInstance.getModel().getType(StartEvent.class);
		Collection<ModelElementInstance> startInstances = modelInstance.getModelElementsByType(startType);

		for (ModelElementInstance startEvent : startInstances) {
			String formKey = startEvent.getAttributeValueNs("http://camunda.org/schema/1.0/bpmn", "formKey");
			if (formKey!=null) {
				Form form = cdaFormService.findByName(formKey);
				if (form!=null) {
					try {
						return FormUtils.buildFullApplication(form);
					} catch (IOException e) {
						throw new TechnicalException(e);
					}
				}
			}
		}
		return null;
	}



	@Override
	public Logger getLogger() {
		return logger;
	}

}
