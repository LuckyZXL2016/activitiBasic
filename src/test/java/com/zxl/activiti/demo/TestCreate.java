package com.zxl.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

/**
 * @author zxl
 * @date 2022/1/11.
 */
public class TestCreate {

    /**
     * 生成 activiti的数据库表
     */
    @Test
    public void testCreateDbTable() {
        // 一、默认方式
        // 使用classpath下的activiti.cfg.xml中的配置创建processEngine（创建mysql的表）
        // ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 二、自定义方式
        // 配置文件的名字可以自定义,bean的名字也可以自定义
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml",
                "processEngineConfiguration");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println(processEngine);
    }
}
