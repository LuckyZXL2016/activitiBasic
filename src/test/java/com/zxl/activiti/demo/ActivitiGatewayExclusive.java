package com.zxl.activiti.demo;

import com.zxl.activiti.demo.listener.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zxl
 * @date 2022/1/16.
 */
public class ActivitiGatewayExclusive {

    ProcessEngine processEngine;
    RepositoryService repositoryService;
    RuntimeService runtimeService;
    TaskService taskService;

    private static final String KEY = "exclusive2";

    @Before
    public void setUp() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
    }

    /**
     * 部署流程定义
     */
    @Test
    public void testDeployment(){
        // 使用RepositoryService进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/evection-exclusive.bpmn20.xml") // 添加bpmn资源
                .name("出差申请流程-排他网关")
                .deploy();
        // 输出部署信息
        System.out.println("流程部署id: " + deployment.getId());
        System.out.println("流程部署名称: " + deployment.getName());
    }

    /**
     * 启动流程实例,设置流程变量的值
     */
    @Test
    public void startProcess(){
        // 创建变量集合
        Map<String, Object> map = new HashMap<>();
        // 创建出差pojo对象
        Evection evection = new Evection();
        // 设置出差天数
        evection.setNum(2d);
        // 定义流程变量，把出差pojo对象放入map
        map.put("evection", evection);
        // 设置assignee的取值，用户可以在界面上设置流程的执行
        map.put("assignee0", "张三");
        map.put("assignee1", "李经理");
        map.put("assignee2", "王总经理");
        map.put("assignee3", "赵财务");
        // 启动流程实例，并设置流程变量的值（把map传入）
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY, map);
        // 输出
        System.out.println("流程实例名称= " + processInstance.getName());
        System.out.println("流程定义id== " + processInstance.getProcessDefinitionId());
    }

    @Test
    public void completTask(){
        // 任务负责人
        String assingee = "李经理";
        // 获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 查询任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskAssignee(assingee)
                .singleResult();
        if(task != null){
            // 根据任务id来完成任务
            taskService.complete(task.getId());
        }

    }
}
