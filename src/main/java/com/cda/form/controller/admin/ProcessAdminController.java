package com.cda.form.controller.admin;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.UserTaskImpl;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cda.form.controller.AbstractController;
import com.cda.form.controller.dto.ProcessDefinitionDto;
import com.cda.form.controller.dto.ProcessElementType;
import com.cda.form.controller.dto.ProcessFormDto;
import com.cda.form.controller.dto.ProcessInstanceDto;
import com.cda.form.model.Form;
import com.cda.form.service.FormService;


@RestController
@RequestMapping("/admin/bpm")
public class ProcessAdminController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(ProcessAdminController.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FormService formService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private SimpMessagingTemplate socketTemplate;


    @RequestMapping(value = "/process", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/process/{processDefinitionId}/forms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ProcessFormDto> getForms(@PathVariable String processDefinitionId) {
    	BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
    	ModelElementType startType = modelInstance.getModel().getType(StartEvent.class);
    	Collection<ModelElementInstance> startInstances = modelInstance.getModelElementsByType(startType);
    	List<ProcessFormDto> result = new ArrayList<>();
    	Map<String, List<ProcessFormDto>> formKeys = new HashMap<>();
    	for (ModelElementInstance startEvent : startInstances) {
    		String activityId = startEvent.getAttributeValue("id");
			String formKey = startEvent.getAttributeValueNs("http://camunda.org/schema/1.0/bpmn", "formKey");
			ProcessFormDto startEventDto = new ProcessFormDto();
			startEventDto.setActivityId(activityId);
			startEventDto.setFormKey(formKey);
			startEventDto.setType(ProcessElementType.START_EVENT);
			result.add(startEventDto);
			if (formKey!=null ) {
				if(!formKeys.containsKey(formKey)) {
					formKeys.put(formKey, new ArrayList<>());
				}
				formKeys.get(formKey).add(startEventDto);
			}
    	}
    	// find all elements of the type task
    	ModelElementType taskType = modelInstance.getModel().getType(Task.class);
    	Collection<ModelElementInstance> taskInstances = modelInstance.getModelElementsByType(taskType);
    	for (ModelElementInstance task : taskInstances ) {
    		if (task instanceof UserTaskImpl) {
    			String taskId = task.getAttributeValue("id");
    			String formKey = task.getAttributeValueNs("http://camunda.org/schema/1.0/bpmn", "formKey");
    			ProcessFormDto taskDto = new ProcessFormDto();
    			taskDto.setActivityId(taskId);
    			taskDto.setFormKey(formKey);
    			taskDto.setType(ProcessElementType.USER_TASK);
    			result.add(taskDto);
    			if (formKey!=null ) {
    				if(!formKeys.containsKey(formKey)) {
    					formKeys.put(formKey, new ArrayList<>());
    				}
    				formKeys.get(formKey).add(taskDto);
    			}
    		}
    	}
    	Collection<Form> forms = formService.findByNames(formKeys.keySet());
    	for(Form form : forms) {
    		for (ProcessFormDto formDto : formKeys.get(form.getName())) {
    			formDto.setFormExists(true);
    		}
    	}
    	return result;
    }

    @RequestMapping(value = "/process/{processDefinitionId}/form", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ProcessFormDto updateForms(@PathVariable String processDefinitionId, @RequestBody ProcessFormDto formDto) {
    	BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
    	ModelElementType activityType = modelInstance.getModel().getType(Task.class);
    	if (formDto.getType()==ProcessElementType.START_EVENT) {
    		activityType = modelInstance.getModel().getType(StartEvent.class);
    	}
    	
    	Collection<ModelElementInstance> activities = modelInstance.getModelElementsByType(activityType);
    	for (ModelElementInstance activity : activities ) {
    		if (formDto.getActivityId().equals(activity.getAttributeValue("id"))) {
    			activity.setAttributeValueNs("http://camunda.org/schema/1.0/bpmn", "formKey", formDto.getFormKey());
    		}
    	}
    	Form form = formService.findByName(formDto.getFormKey());
    	if (form!=null) {
    		formDto.setFormExists(true);
    	}
    	return formDto;
    }
    
    @RequestMapping(value = "/process/{processDefinitionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String getProcessDefinition(@PathVariable String processDefinitionId) {
    	BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
    	return Bpmn.convertToString(modelInstance);
    }
    
    @RequestMapping(value = "/process/{processName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessDefinitionDto setProcessDefinition(@PathVariable String processName, @RequestBody String xml) {
    	BpmnModelInstance modelInstance = Bpmn.readModelFromStream(IOUtils.toInputStream(xml, Charset.defaultCharset()));
    	DeploymentBuilder deploymentbuilder = repositoryService.createDeployment();
    	deploymentbuilder.addModelInstance(processName+".bpmn", modelInstance);
    	DeploymentEntity deployment = (DeploymentEntity) deploymentbuilder.deploy();
    	ProcessDefinitionEntity processDef = (ProcessDefinitionEntity) deployment.getDeployedArtifacts().values().iterator().next().get(0);
    	ProcessDefinitionDto result = new ProcessDefinitionDto();
    	BeanUtils.copyProperties(processDef, result);
    	return result;
    }
    
    @RequestMapping(value = "/process/{processDefinitionId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteProcessDefinition(@PathVariable String processDefinitionId) {
    	repositoryService.deleteProcessDefinition(processDefinitionId);
    }
    
    @RequestMapping(value = "/process/{processDefinitionId}/instances", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ProcessInstanceDto> getRunningProcessInstanceList(@PathVariable String processDefinitionId) {

        List<ProcessInstance> runningProcessList = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).active().list();
        List<ProcessInstanceDto> result = new ArrayList<>();
        for (ProcessInstance pi: runningProcessList ) {
            ProcessInstanceDto dto= new ProcessInstanceDto();
            BeanUtils.copyProperties(pi , dto);
            result.add(dto);
        }
        return result;

    }



    @Override
    public Logger getLogger() {
        return logger;
    }

}
