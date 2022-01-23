package com.zxl.activiti.demo.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author zxl
 * @Description 监听器
 * @date 2022/1/16.
 */
public class MyTaskListener implements TaskListener {

    /**
     * 指定负责人
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        // 判断当前的任务 1）创建申请 2）create事件
        if ("创建申请".equals(delegateTask.getName())
        && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("张三");
        }
    }
}
