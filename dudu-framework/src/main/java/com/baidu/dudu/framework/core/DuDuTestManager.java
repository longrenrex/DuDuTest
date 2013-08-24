package com.baidu.dudu.framework.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.plugin.Plugin;

/**
 * @author rzhao
 */
public class DuDuTestManager {

    private static Logger logger = LoggerFactory.getLogger(DuDuTestManager.class);

    private static ApplicationContext context;

    public DuDuTestManager() {
        logger.debug("DuDuTestManager initializes...");
    }

    public TestInteraction getTestInteraction() {
        TestInteraction interaciton = (TestInteraction) context.getBean("duduTestInteractionImpl");

        if (interaciton == null) {
            throw new DuDuException("initialize DuDuTestInteraction error!!");
        }

        interaciton.setTestManager(this);

        return interaciton;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public Plugin getPlugin(String name) {
        return (Plugin) context.getBean(name.toLowerCase() + "Plugin");
    }

    public Object getBean(String name) {
        return context.getBean(name);
    }
}
