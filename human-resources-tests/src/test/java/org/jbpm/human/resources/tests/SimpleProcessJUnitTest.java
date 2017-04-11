/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.human.resources.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

/**
 *
 * @author salaboy
 */
public class SimpleProcessJUnitTest extends JbpmJUnitBaseTestCase {

	  public SimpleProcessJUnitTest() {
	        super(true, true, "org.jbpm.sample");
	    }

	    @Test
	    public void simpleProcessTest() {
	        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
	        resources.put("hiring.bpmn2", ResourceType.BPMN2);
	        createRuntimeManager(resources, "simple-no-cdi-test");
	        RuntimeEngine engine = getRuntimeEngine();
	        assertNotNull(engine);

	        KieSession ksession = engine.getKieSession();
	        
	        
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("name", "salaboy");
	        ProcessInstance processInstance = ksession.startProcess("hiring", params);

	        assertNotNull(processInstance);

	        TaskService taskService = engine.getTaskService();
	        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("kuba", "en-UK");

	        assertTrue(!tasksAssignedAsPotentialOwner.isEmpty());
	        TaskSummary taskSummary = tasksAssignedAsPotentialOwner.get(0);
	        assertEquals("HR Interview", taskSummary.getName());
	        assertEquals("Candidate: salaboy", taskSummary.getDescription());
	        
	        taskService.claim(taskSummary.getId(), "kuba");

	        taskService.start(taskSummary.getId(), "kuba");

	        Task hRInterviewTask = taskService.getTaskById(taskSummary.getId());

	        long contentId = hRInterviewTask.getTaskData().getDocumentContentId();
	        assertTrue(contentId != -1);

	        Content content = taskService.getContentById(contentId);
	        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
	        if (!(unmarshalledObject instanceof Map)) {
	            fail("The variables should be a Map");
	        }
	        
	        Map<String, Object> results = new HashMap<String, Object>();
			results.put("out_age", "25");
			results.put("out_mail", "hehe@hehe.pl");
			results.put("out_score", "5");
			
			taskService.complete(hRInterviewTask.getId(), "kuba", results);
			
			tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("kuba", "en-UK");
			
	        assertTrue(!tasksAssignedAsPotentialOwner.isEmpty());
	        taskSummary = tasksAssignedAsPotentialOwner.get(0);
	        assertEquals("Tech Interview", taskSummary.getName());
	        
	        taskService.claim(taskSummary.getId(), "kuba");

	        taskService.start(taskSummary.getId(), "kuba");
	        
	        Task TechInterviewTask = taskService.getTaskById(taskSummary.getId());
	        
	        contentId = TechInterviewTask.getTaskData().getDocumentContentId();
	        assertTrue(contentId != -1);

	        content = taskService.getContentById(contentId);
	        unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
	        if (!(unmarshalledObject instanceof Map)) {
	            fail("The variables should be a Map");
	        }
	        
	        results = new HashMap<String, Object>();
			results.put("out_skills", "Pretty cool");
			results.put("out_score", "6");
			results.put("out_twitter", "Twittuje coś tam");
			
			taskService.complete(TechInterviewTask.getId(), "kuba", results);
			
			tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
			
	        assertTrue(!tasksAssignedAsPotentialOwner.isEmpty());
	        taskSummary = tasksAssignedAsPotentialOwner.get(0);
	        assertEquals("Create Proposal", taskSummary.getName());
	        
	        taskService.claim(taskSummary.getId(), "mary");

	        taskService.start(taskSummary.getId(), "mary");
	        
	        Task CreateProposalTask = taskService.getTaskById(taskSummary.getId());
	        
	        contentId = CreateProposalTask.getTaskData().getDocumentContentId();
	        assertTrue(contentId != -1);

	        content = taskService.getContentById(contentId);
	        unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
	        if (!(unmarshalledObject instanceof Map)) {
	            fail("The variables should be a Map");
	        }
	        
	        results = new HashMap<String, Object>();
			results.put("out_offering", "5000");
			
			taskService.complete(CreateProposalTask.getId(), "mary", results);
	        
			assertNodeTriggered(processInstance.getId(), "HR Interview", 
					"Tech Interview", "Create Proposal", "Send Proposal");
			
			tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
			
	        assertTrue(!tasksAssignedAsPotentialOwner.isEmpty());
	        taskSummary = tasksAssignedAsPotentialOwner.get(0);
	        assertEquals("Sign Contract", taskSummary.getName());
	        
	        taskService.claim(taskSummary.getId(), "mary");

	        taskService.start(taskSummary.getId(), "mary");
	        
	        Task SignContractTask = taskService.getTaskById(taskSummary.getId());
	        
	        contentId = SignContractTask.getTaskData().getDocumentContentId();
	        assertTrue(contentId != -1);
	        
	        content = taskService.getContentById(contentId);
	        unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
	        if (!(unmarshalledObject instanceof Map)) {
	            fail("The variables should be a Map");
	        }
	        
	        results = new HashMap<String, Object>();
			results.put("out_signed", "true");
			
			taskService.complete(SignContractTask.getId(), "mary", results);
			
			assertNodeTriggered(processInstance.getId(), "Sign Contract", 
					"Tweet New Hire");
			
			assertProcessInstanceNotActive(processInstance.getId(), ksession);
			System.out.println("Process instance completed");

	    }

}
