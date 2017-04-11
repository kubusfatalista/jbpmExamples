package org.jbpm.spring.boot;

import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.MavenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deployment")
public class DeployController {
	
	@Autowired
	private DeploymentService deploymentService;
	
	protected static final String ARTIFACT_ID = "test-module";
	protected static final String GROUP_ID = "org.jbpm.test";
	protected static final String VERSION = "1.0.0";

    @RequestMapping("/")
    public Collection<String> index() {
    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	Collection<String> units = new ArrayList<String>();
    	
    	for (DeployedUnit dUnit : deployed) {
    		units.add(dUnit.getDeploymentUnit().getIdentifier());
    	}
        return units;
    }
    
    @RequestMapping(value="/deploy", method=RequestMethod.POST)
    public String deploy(@RequestParam("id")String id, @RequestParam(value="strategy", defaultValue="SINGLETON") String strategy) {
    	String outcome = "Deployment " + id + " deployed successfully";
    	
    	String[] gav = id.split(":");
    	
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        File kjar = new File("src/main/resources/kjar/jbpm-module.jar");
        File pom = new File("src/main/resources/kjar/pom.xml");
        MavenRepository repository = getMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(unit);        
    	
    	return outcome;
    }

    @RequestMapping(value="/undeploy", method=RequestMethod.POST)
    public String undeploy(@RequestParam("id")String id) {
    	String outcome = "";
    	DeployedUnit deployed = deploymentService.getDeployedUnit(id);
    	if (deployed != null) {
    		deploymentService.undeploy(deployed.getDeploymentUnit());
    		outcome = "Deployment " + id + " undeployed successfully";
    	} else {
    		outcome = "No deployment " + id + " found";
    	}
    	return outcome;
    }
}
