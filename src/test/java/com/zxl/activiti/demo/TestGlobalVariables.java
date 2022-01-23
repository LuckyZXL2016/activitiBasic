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
public class TestGlobalVariables {

    ProcessEngine processEngine;
    RepositoryService repositoryService;
    RuntimeService runtimeService;
    TaskService taskService;

    @Before
    public void setUp() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
    }

    @Test
    public void testDeployment() {
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程-variables-complete")
                .addClasspathResource("bpmn/evection-global.bpmn20.xml")
                .deploy();
        System.out.println("流程部署id= " + deploy.getId());
        System.out.println("流程部署name= " + deploy.getName());
    }

    /**
     * 启动流程实例,设置流程变量的值
     */
    @Test
    public void testStartProcess(){
        // 流程定义key
        String key = "myEvection3";
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
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, map);
        // 输出
        System.out.println("流程定义id= " + processInstance.getProcessDefinitionId());
        System.out.println("流程实例name= " + processInstance.getName());
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completTask() {
        // 任务id
        String key = "myEvection3";
        // 任务负责人
//        String assingee = "张三";
        String assingee = "李经理";
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskAssignee(assingee)
                .singleResult();
        // 根据任务id和任务负责人查询当前任务
        if(task != null){
            // 完成任务是，设置流程变量的值
            taskService.complete(task.getId());
            System.out.println("任务执行完成");
        }
    }

    /**
     * 完成个人任务，设置流程变量（任务办理时设置）
     */
    @Test
    public void completTask2() {
        // 任务id
        String key = "myEvection3";
        // 任务负责人
//        String assingee = "张三";
        String assingee = "李经理";
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskAssignee(assingee)
                .singleResult();
        // 设置流程变量
        Map<String, Object> map = new HashMap<>();
        Evection evection = new Evection();
        evection.setNum(2d);
        map.put("evection", evection);
        // 根据任务id和任务负责人查询当前任务
        if(task != null){
            // 完成任务是，设置流程变量的值
            taskService.complete(task.getId(), map);
            System.out.println("任务执行完成");
        }
    }

    /**
     * 通过流程实例id设置全局变量，该流程实例必须未执行完成
     */
    @Test
    public void setGlobalVariableByExecutionId(){
        // 当前流程实例执行 id，通常设置为当前执行的流程实例
        String executionId="2601";
        // 创建出差pojo对象
        Evection evection = new Evection();
        // 设置天数
        evection.setNum(3d);
        // 通过流程实例 id设置流程变量
        runtimeService.setVariable(executionId, "evection", evection);
        // 一次设置多个值
        // runtimeService.setVariables(executionId, variables)
    }

    /**
     * 通过当前任务设置
     */
    @Test
    public void setGlobalVariableByTaskId(){
        // 当前待办任务id
        String taskId="1404";
        // 获取processEngine
        Evection evection = new Evection();
        evection.setNum(3d);
        // 通过任务设置流程变量
        taskService.setVariable(taskId, "evection", evection);
        // 一次设置多个值
        // taskService.setVariables(taskId, variables)
    }
}
