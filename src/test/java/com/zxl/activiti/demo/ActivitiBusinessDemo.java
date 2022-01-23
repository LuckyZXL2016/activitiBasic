package com.zxl.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * @author zxl
 * @date 2022/1/12.
 */
public class ActivitiBusinessDemo {

    /**
     * 启动流程实例，添加businessKey
     */
    @Test
    public void addBusinessKey() {
        // 1、得到ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、得到RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3、启动流程实例，同时还要指定业务标识businessKey，也就是出差申请单id，这里是1001
        ProcessInstance processInstance = runtimeService.
                startProcessInstanceByKey("myEvection", "1001");
        // 4、输出processInstance相关属性
        System.out.println("业务id = " + processInstance.getBusinessKey());
    }

    /**
     * 全部流程实例挂起与激活
     */
    @Test
    public void SuspendAllProcessInstance() {
        // 1、获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3、查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        // 4、得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();
        // 5、流程定义id
        String processDefinitionId = processDefinition.getId();
        // 6、判断是否为暂停
        if (suspended) {
            // 7、如果是暂停，可以执行激活操作 ,参数1 ：流程定义id ，参数2：是否激活，参数3：激活时间
            repositoryService.activateProcessDefinitionById(processDefinitionId,
                    true,
                    null
            );
            System.out.println("流程定义：" + processDefinitionId + ",已激活");
        } else {
            // 8、如果是激活状态，可以暂停，参数1 ：流程定义id ，参数2：是否暂停，参数3：暂停时间
            repositoryService.suspendProcessDefinitionById(processDefinitionId,
                    true,
                    null);
            System.out.println("流程定义：" + processDefinitionId + ",已挂起");
        }
    }

    /**
     * 单个流程实例挂起与激活
     */
    @Test
    public void SuspendSingleProcessInstance(){
        // 1、获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3、查询流程定义的对象
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId("15001")
                .singleResult();
        // 4、得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();
        // 5、流程定义id
        String processDefinitionId = processInstance.getId();
        // 6、判断是否为暂停
        if (suspended) {
            // 7、如果是暂停，可以执行激活操作 ,参数：流程定义id
            runtimeService.activateProcessInstanceById(processDefinitionId);
            System.out.println("流程定义：" + processDefinitionId + ",已激活");
        } else {
            // 8、如果是激活状态，可以暂停，参数：流程定义id
            runtimeService.suspendProcessInstanceById( processDefinitionId);
            System.out.println("流程定义：" + processDefinitionId + ",已挂起");
        }

    }
}