package com.zxl.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zxl
 * @date 2022/1/16.
 */
public class TestAssigneeUel {

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
                .name("出差申请流程-uel")
                .addClasspathResource("bpmn/evection-uel.bpmn20.xml")
                .deploy();
        System.out.println("流程部署id= " + deploy.getId());
        System.out.println("流程部署name= " + deploy.getName());
    }

    @Test
    public void startAssigneeUel() {
        runtimeService = processEngine.getRuntimeService();
        // 设定assignee的值，用来替换uel表达值
        Map<String, Object> assigneeMap = new HashMap<>();
        assigneeMap.put("assignee0", "张三");
        assigneeMap.put("assignee1", "李经理");
        assigneeMap.put("assignee2", "王总经理");
        assigneeMap.put("assignee3", "赵总财务");
        runtimeService.startProcessInstanceByKey("myEvection1", assigneeMap);
    }
}
