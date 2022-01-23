package com.zxl.activiti.demo;

import com.zxl.activiti.demo.listener.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zxl
 * @date 2022/1/16.
 */
public class TestLocalVariables {

    ProcessEngine processEngine;
    RepositoryService repositoryService;
    RuntimeService runtimeService;
    TaskService taskService;
    HistoryService historyService;

    @Before
    public void setUp() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
    }

    @Test
    public void setLocalVariableByTaskId(){
        // 当前待办任务id
        String taskId="1404";
        Evection evection = new Evection ();
        evection.setNum(3d);
        // 通过任务设置流程变量
        taskService.setVariableLocal(taskId, "evection", evection);
        // 一次设置多个值
        // taskService.setVariablesLocal(taskId, variables)
    }

    @Test
    public void queryLocalVariables() {
        // 创建历史任务查询对象
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
        // 查询结果包括 local变量
        List<HistoricTaskInstance> list = historicTaskInstanceQuery.includeTaskLocalVariables().list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("==============================");
            System.out.println("任务id: " + historicTaskInstance.getId());
            System.out.println("任务名称: " + historicTaskInstance.getName());
            System.out.println("任务负责人: " + historicTaskInstance.getAssignee());
            System.out.println("任务local变量: " + historicTaskInstance.getTaskLocalVariables());
        }
    }
}
