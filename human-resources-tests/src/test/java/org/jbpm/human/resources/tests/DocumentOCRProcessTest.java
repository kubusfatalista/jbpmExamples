package org.jbpm.human.resources.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class DocumentOCRProcessTest extends JbpmJUnitBaseTestCase {
	
	public DocumentOCRProcessTest() {
        super(true, true, "org.jbpm.sample");
    }
	
	@Test
    public void documentOcrProcessTest() {
		RuntimeManager manager = createRuntimeManager("DocumentOCR.bpmn2");
        
        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine(EmptyContext.get());

        // get access to KieSession instance
        KieSession ksession = runtimeEngine.getKieSession();

        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());

        // start process
        
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("currentDocument","Pozew o piniondze");

        ProcessInstance processInstance = ksession.startProcess("defaultPackage.DocumentOCR", params);
        
//        HashMap<String, Object> results = new HashMap<String, Object>();
//		results.put("document", "jakis document");
	
        /**
         * 
         *  Trzeba setowac taka funkcja bo inaczej nie ogarnie 
         *  kcontext.setVariable("currentBarcode", (Long)barcodeList.get(0));
         * 
         */
        
        // ((WorkflowProcessInstance) processInstance).setVariable("DocumentNumber", "0"); nie wiem czy sie rozni to od tego wyzyj
        
     // wyswietla sie nawet, elo
//		List<Long> variable2 = (List<Long>) ((WorkflowProcessInstance) processInstance).getVariable("barcodes");

//		System.out.println(variable);
//		System.out.println(variable2);
        
        TaskService taskService = runtimeEngine.getTaskService();
        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("kuba", "en-UK");
        
        assertTrue(!tasksAssignedAsPotentialOwner.isEmpty());
        TaskSummary taskSummary = tasksAssignedAsPotentialOwner.get(0);
        assertEquals("Test User Form", taskSummary.getName());
        
        taskService.claim(taskSummary.getId(), "kuba");

        taskService.start(taskSummary.getId(), "kuba");

        Task testHumanTask = taskService.getTaskById(taskSummary.getId());

        long contentId = testHumanTask.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        
        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        if (!(unmarshalledObject instanceof Map)) {
            fail("The variables should be a Map");
        }
        
        Map<String, Object> results = new HashMap<String, Object>();
		results.put("out_testResult", "25");
		
		taskService.complete(testHumanTask.getId(), "kuba", results);
		
		System.out.println("Wynigi na koncu " + ((Integer)((RuleFlowProcessInstance) processInstance).getVariable("testResult")));
		
		// nie mam pojecia jak sie dostac do tych wynikow na koncu
		
//        assertNodeTriggered(processInstance.getId(), "StartProcess", "Service Task 1", "End Event 1");
        
        manager.disposeRuntimeEngine(runtimeEngine);
		manager.close();
	}

}
