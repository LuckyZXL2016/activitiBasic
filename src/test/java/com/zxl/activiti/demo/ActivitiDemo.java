package com.zxl.activiti.demo;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @author zxl
 * @date 2022/1/12.
 */
public class ActivitiDemo {

    /**
     * 测试流程部署
     */
    @Test
    public void testDeployment() {
        // 1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3、使用RepositoryService进行部署，定义一个流程的名字，把bpmn和png部署到数据中
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程")
                .addClasspathResource("bpmn/evection.bpmn20.xml") // 添加bpmn资源
                // .addClasspathResource("bpmn/evection.png")  // 添加png资源
                .deploy();
        // 4、输出部署信息
        System.out.println("流程部署: " + deploy.getId());
        System.out.println("流程部署name: " + deploy.getName());
    }

    /**
     * 压缩包部署方式
     */
    @Test
    public void deployProcessByZip() {
        // 1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、定义zip输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bpmn/evection.zip");
        assert inputStream != null;
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 3、获取repositoryService
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        // 4、流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程部署id: " + deployment.getId());
        System.out.println("流程部署名称: " + deployment.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess(){
        // 1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3、根据流程定义Id启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myEvection");
        // 4、输出内容
        System.out.println("流程定义id: " + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id: " + processInstance.getId());
        System.out.println("当前活动Id: " + processInstance.getActivityId());
    }

    /**
     * 查询当前个人待执行的任务
     */
    @Test
    public void testFindPersonalTaskList() {
        // 1、任务负责人
        String assignee = "zhangsan";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、创建TaskService
        TaskService taskService = processEngine.getTaskService();
        // 3、根据流程key 和 任务负责人 查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myEvection") // 流程Key
                .taskAssignee(assignee) // 只查询该任务负责人的任务
                .list();
        for (Task task : list) {
            System.out.println("流程实例id: " + task.getProcessInstanceId());
            System.out.println("任务id: " + task.getId());
            System.out.println("任务负责人: " + task.getAssignee());
            System.out.println("任务名称: " + task.getName());
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void completTask(){
        // 1、获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3、根据流程key 和 任务的负责人 查询任务
        // 4、返回一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myEvection") //流程Key
                .taskAssignee("rose")  // 要查询的负责人
                .singleResult();
        // 5、完成任务,参数：任务id
        taskService.complete(task.getId());
    }

    /**
     * 查询流程定义
     */
    @Test
    public void queryProcessDefinition(){
        // 1、获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、得到ProcessDefinitionQuery 对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        // 3、查询出当前所有的流程定义
        //      条件：processDefinitionKey =evection
        //      orderByProcessDefinitionVersion 按照版本排序
        //      desc倒叙
        //      list 返回集合
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey("myEvection")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        // 4、输出流程定义信息
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义id: " + processDefinition.getId());
            System.out.println("流程定义name: " + processDefinition.getName());
            System.out.println("流程定义key: " + processDefinition.getKey());
            System.out.println("流程定义Version: " + processDefinition.getVersion());
            System.out.println("流程部署ID: " + processDefinition.getDeploymentId());
        }
    }

    /**
     * 删除流程部署信息
     * act_ge_bytearray
     * act_re_deployment
     * act_re_procdef
     * 说明：
     *  1) 使用repositoryService删除流程定义，历史表信息不会被删除
     *  2）如果该流程定义下没有正在运行的流程，则可以用普通删除
     * 如果该流程定义下存在已经运行的流程，使用普通删除报错，可用级联删除方法将流程及相关记录全部删除
     * 先删除没有完成流程节点，最后就可以完全删除流程定义信息
     * 项目开发中级联删除操作一般只开放给超级管理员使用
     */
    @Test
    public void deleteDeployment() {
        // 1、流程部署id
        String deploymentId = "1";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、通过流程引擎获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3、删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        repositoryService.deleteDeployment(deploymentId);
        // 4、设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式
        // repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 下载 资源文件
     * 使用Activiti提供的api，下载资源文件，保存到文件目录
     */
    @Test
    public void getDeployment() throws IOException {
        // 1、得到引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3、得到查询器：ProcessDefinitionQuery，设置查询条件,得到想要的流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        // 4、通过流程定义信息，得到部署ID
        String deploymentId = processDefinition.getDeploymentId();
        // 5、通过repositoryService的方法，实现读取图片信息和bpmn信息
        // 5.1、获取png图片的流
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getDiagramResourceName());
        // 5.2、获取bpmn的流
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        // 6、构造OutputStream流
        File file_png = new File("d:/evection01.png");
        File file_bpmn = new File("d:/evection01.bpmn.xml");
        FileOutputStream bpmnOut = new FileOutputStream(file_bpmn);
        FileOutputStream pngOut = new FileOutputStream(file_png);
        // 7、输入流，输出流的转换
        IOUtils.copy(pngInput,pngOut);
        IOUtils.copy(bpmnInput,bpmnOut);
        // 8、关闭流
        pngOut.close();
        bpmnOut.close();
        pngInput.close();
        bpmnInput.close();
    }

    /**
     * 查看历史信息
     */
    @Test
    public void findHistoryInfo(){
        // 1、获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、获取HistoryService
        HistoryService historyService = processEngine.getHistoryService();
        // 3、获取 actinst表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        // 4、查询 actinst表，条件：根据 InstanceId 查询
        // 4.1、instanceQuery.processInstanceId("2501");
        // 4.2、查询 actinst表，条件：根据 DefinitionId 查询
        instanceQuery.processDefinitionId("myEvection:1:4");
        // 5、增加排序操作,orderByHistoricActivityInstanceStartTime 根据开始时间排序 asc 升序
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
        // 6、查询所有内容
        List<HistoricActivityInstance> activityInstanceList = instanceQuery.list();
        // 7、输出
        for (HistoricActivityInstance hi : activityInstanceList) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("<==========================>");
        }
    }
}
