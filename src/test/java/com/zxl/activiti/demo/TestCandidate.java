package com.zxl.activiti.demo;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zxl
 * @date 2022/1/16.
 */
public class TestCandidate {

    ProcessEngine processEngine;
    RepositoryService repositoryService;
    RuntimeService runtimeService;
    TaskService taskService;

    private static final String KEY = "testCandidate";

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
                .name("出差申请流程-Candidate")
                .addClasspathResource("bpmn/evection-candidate.bpmn20.xml")
                .deploy();
        System.out.println("流程部署id= " + deploy.getId());
        System.out.println("流程部署name= " + deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY);
        // 输出
        System.out.println("流程定义id= " + processInstance.getProcessDefinitionId());
        System.out.println("流程实例name= " + processInstance.getName());
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completTask() {
        // 任务负责人
        String assingee = "zhangsan";
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
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
     * 根据候选人查询组任务
     */
    @Test
    public void findGroupTaskList() {
        // 任务候选人
        String candidateUser = "lisi";
        // 查询组任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskCandidateUser(candidateUser) // 根据候选人查询
                .list();
        for (Task task : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id: " + task.getProcessInstanceId());
            System.out.println("任务id: " + task.getId());
            System.out.println("任务负责人: " + task.getAssignee());
            System.out.println("任务名称: " + task.getName());
        }
    }

    /**
     * 拾取组任务，候选人员拾取组任务后该任务变为自己的个人任务
     */
    @Test
    public void claimTask(){
        // 要拾取的任务id
        String taskId = "92502";
        // 任务候选人id
        String userId = "lisi";
        // 拾取任务
        // 即使该用户不是候选人也能拾取(建议拾取时校验是否有资格)
        // 校验该用户有没有拾取任务的资格
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(userId) // 根据候选人查询
                .singleResult();
        if( task!=null ) {
            // 拾取任务
            taskService.claim(taskId, userId);
            System.out.println("taskId-" + taskId + "任务拾取成功");
        }
    }

    /**
     * 查询方式同个人任务查询
     */
    @Test
    public void findPersonalTaskList() {
        // 任务负责人
        String assignee = "lisi";
        // 创建TaskService
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskAssignee(assignee)
                .list();
        for (Task task : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id: " + task.getProcessInstanceId());
            System.out.println("任务id: " + task.getId());
            System.out.println("任务负责人: " + task.getAssignee());
            System.out.println("任务名称: " + task.getName());
        }
    }

    /**
     * 办理个人任务
     */
    @Test
    public void completeTask(){
        // 任务ID
        String taskId = "92502";
        // 获取processEngine
        taskService.complete(taskId);
        System.out.println("完成任务: " + taskId);
    }

    /**
     * 归还组任务
     * 如果个人不想办理该组任务，可以归还组任务，归还后该用户不再是该任务的负责人
     */
    @Test
    public void testAssigneeToGroupTask() {
        // 当前待办任务
        String taskId = "92502";
        // 任务负责人
        String userId = "lisi";
        // 校验userId是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if (task != null) {
            // 如果设置为null，归还组任务，该任务没有负责人
            taskService.setAssignee(taskId, null);
        }
    }

    /**
     * 任务交接
     * 任务负责人将任务交给其它候选人办理该任务
     */
    @Test
    public void setAssigneeToCandidateUser() {
        // 当前待办任务
        String taskId = "92502";
        // 任务负责人
        String userId = "lisi";
        // 将此任务交给其它候选人办理该 任务
        String candidateuser = "wangwu";
        // 校验userId是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if (task != null) {
            taskService.setAssignee(taskId, candidateuser);
        }
    }
}
