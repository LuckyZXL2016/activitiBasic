package com.zxl.activiti.business;

import com.zxl.activiti.utils.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zxl
 * @date 2022/1/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActSpringBootTest {

    @Autowired
    ProcessRuntime processRuntime;

    @Autowired
    TaskRuntime taskRuntime;

    @Autowired
    SecurityUtil securityUtil;

    /**
     * 查看流程定义内容
     */
    @Test
    public void findProcess() {
        securityUtil.logInAs("jack");
        // 流程定义的分页对象
        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));
        System.out.println("可用的流程定义数量：" + processDefinitionPage.getTotalItems());
        for (ProcessDefinition pd : processDefinitionPage.getContent()) {
            System.out.println("流程定义：" + pd);
        }
    }

    /**
     * 启动流程
     */
    @Test
    public void startProcess() {
        securityUtil.logInAs("system");
        ProcessInstance pi = processRuntime.start(ProcessPayloadBuilder.start()
                .withProcessDefinitionKey("springDemo")
                .build());
        System.out.println("流程实例ID：" + pi.getId());
    }

    /**
     **查询任务，并完成自己的任务
     **/
    @Test
    public void testTask() {
        securityUtil.logInAs("jack");
        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0,10));
        if (taskPage.getTotalItems()>0) {
            for (Task task : taskPage.getContent()) {
                taskRuntime.claim(TaskPayloadBuilder
                        .claim()
                        .withTaskId(task.getId()).build());
                System.out.println("任务：" + task);
                taskRuntime.complete(TaskPayloadBuilder
                        .complete()
                        .withTaskId(task.getId()).build());
            }
        }
        Page<Task> taskPage2 = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage2.getTotalItems()>0) {
            System.out.println("任务：" + taskPage2.getContent());
        }
    }
}
