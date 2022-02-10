package com.cda.form.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cda.form.controller.dto.ProcessDefinitionDto;
import com.cda.form.controller.dto.ProcessInstanceDto;


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


    @RequestMapping(value = "/processes", method = RequestMethod.GET, produces = "application/json")
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

    @RequestMapping(value = "/processes/{id}/usertasks", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getTasks(@PathVariable String id) {
    	BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(id);
    	// find all elements of the type task
    	ModelElementType taskType = modelInstance.getModel().getType(Task.class);
    	Collection<ModelElementInstance> taskInstances = modelInstance.getModelElementsByType(taskType);
    	List<String> result = new ArrayList<>();
    	for (ModelElementInstance task : taskInstances ) {
    		result.add(task.getAttributeValueNs("http://camunda.org/schema/1.0/bpmn", "formKey"));
    	}
    	return result;
    }
    
    @RequestMapping(value = "/process/{key}/start", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ProcessInstanceDto startProcessInstanceByKey(@PathVariable String key, @RequestBody Map<String, Object> variables){
        //identityService.setAuthenticatedUserId(SecurityUtils.getConnectedUser().getUsername());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);
        logger.info("New process instance initiated. Process instance id is {}", processInstance.getId());

        ProcessInstanceDto dto= new ProcessInstanceDto();
        BeanUtils.copyProperties(processInstance, dto);
        return dto;
    }

    @RequestMapping(value = "/task/{id}/execute", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void executeTask(@PathVariable String id, @RequestBody Map<String, Object> variables){
        //identityService.setAuthenticatedUserId(SecurityUtils.getConnectedUser().getUsername());
        taskService.complete(id, variables);
    }

    @RequestMapping(value = "/process/{key}/instances", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<ProcessInstanceDto> getRunningProcessInstanceList(@PathVariable String key) {

        List<ProcessInstance> runningProcessList = runtimeService.createProcessInstanceQuery().processDefinitionKey(key).active().list();
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
