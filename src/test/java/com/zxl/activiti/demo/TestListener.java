package com.zxl.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zxl
 * @date 2022/1/16.
 */
public class TestListener {

    ProcessEngine processEngine;
    RepositoryService repositoryService;
    RuntimeService runtimeService;

    @Before
    public void setUp() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = processEngine.getRepositoryService();
    }

    @Test
    public void testDeployment() {
        Deployment deploy = repositoryService.createDeployment()
                .name("测试监听器")
                .addClasspathResource("bpmn/demo-listen.bpmn20.xml")
                .deploy();
        System.out.println("流程部署id= " + deploy.getId());
        System.out.println("流程部署name= " + deploy.getName());
    }

    @Test
    public void startDemoListener() {
        runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("testListener");
    }
}
