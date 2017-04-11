package com.sample;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class Hello extends JbpmJUnitBaseTestCase {
	
	
	@Test
	public void helloTest() {

//		PoolingDataSource ds = new PoolingDataSource();
//		ds.setUniqueName("jdbc/jbpm-ds");
//		ds.setClassName("com.mysql.cj.jdbc.MysqlXADataSource");
//		ds.setMaxPoolSize(3);
//		ds.setAllowLocalTransactions(true);
//		ds.getDriverProperties().put("user", "jbpm");
//		ds.getDriverProperties().put("password", "jbpm");
//		ds.getDriverProperties().put("URL", "jdbc:mysql://localhost:3306/jbpm?useSSL=false");
//		ds.getDriverProperties().put("driverClassName", "mysql-connector-java-6.0.2.jar");
//        ds.setEnableJdbc4ConnectionTest(true);
//        System.out.println(ds.isEnableJdbc4ConnectionTest());
//        
//        ds.init();
//      try {
//			Connection connection = ds.getConnection();
//			PreparedStatement statement = connection.prepareStatement("Select * from jbpm.task;");
//			ResultSet result = statement.executeQuery();
//			System.out.println(result);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
			
		RuntimeManager manager = createRuntimeManager("HelloWorld.bpmn2");
        
        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine(EmptyContext.get());

        // get access to KieSession instance
        KieSession ksession = runtimeEngine.getKieSession();

        // start process
        ProcessInstance processInstance = ksession.startProcess("com.sample.HelloWorld");
        
        // check what nodes have been triggered
        assertNodeTriggered(processInstance.getId(), "Start", "Hello", "End");
		
//		KieHelper kieHelper = new KieHelper();
//		KieBase kieBase = kieHelper.addResource(ResourceFactory.newClassPathResource("HelloWorld.bpmn2")).build();
//		KieSession ksession = kieBase.newKieSession();
//		ksession.startProcess("com.sample.HelloWorld");
        
        manager.disposeRuntimeEngine(runtimeEngine);
		manager.close();
		
	}
	
	public Hello() {
        // setup data source, enable persistence
        super(false, false);
    }

}
