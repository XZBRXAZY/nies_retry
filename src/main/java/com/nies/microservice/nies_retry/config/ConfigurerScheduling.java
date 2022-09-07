package com.nies.microservice.nies_retry.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @Author dck
 * @Date 2020/11/15
 * @Version V1.0
 **/
@Configuration
@EnableScheduling
public abstract class ConfigurerScheduling implements SchedulingConfigurer {


    /**
     * @brief 定时任务周期表达式
     */
    private String cron;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskScheduler());
        scheduledTaskRegistrar.addTriggerTask(
                //执行定时任务
                () -> {
                    processTask();
                },
                //设置触发器
                triggerContext -> {
                    // 初始化定时任务周期
                    if (StringUtils.isEmpty(cron)) {
                        cron = getCron();
                    }
                    CronTrigger trigger = new CronTrigger(cron);
                    return trigger.nextExecutionTime(triggerContext);
                }
        );
    }

    /**
     * 设置TaskScheduler用于注册计划任务
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        //设置线程名称
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("插座数据获取-pool-%d").build();
        //创建线程池
        return Executors.newScheduledThreadPool(5, namedThreadFactory);
    }

    /**
     * @brief 任务的处理函数
     * 本函数需要由派生类根据业务逻辑来实现
     */
    protected abstract void processTask();


    /**
     * @return String
     * @brief 获取定时任务周期表达式
     * 本函数由派生类实现，从配置文件，数据库等方式获取参数值
     */
    protected abstract String getCron();
}