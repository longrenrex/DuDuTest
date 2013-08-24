package com.baidu.dudu.framework.interaction;


import java.util.concurrent.Callable;

import com.baidu.dudu.framework.core.DuDuTestManager;
import com.baidu.dudu.framework.message.ComparisonResult;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.plugin.Plugin;

/**
 * @author rzhao
 */
public interface TestInteraction {

    /**
     * Set plugin
     * 
     * @param name
     * @return
     */
    Plugin getPlugin(final String name);

    /**
     * Get DuDuTestManager
     * 
     * @return DuDuTestManager
     */
    DuDuTestManager getTestManager();

    /**
     * Set DuDuTestManager
     * 
     * @param testManager
     */
    void setTestManager(DuDuTestManager testManager);

    /**
     * Add task
     * 
     * @param task
     */
    void addTask(Callable<Object> task);

    /**
     * check tasks
     */
    public void checkTasks();

    /**
     * Compare results
     * 
     * @param result
     */
    void analyticResult(final DuDuMessage message, final ComparisonResult result);

    /**
     * Invoke before test case setup
     */
    void beforeSetUp();

    /**
     * Invoke after test case set up
     */
    void afterSetUp();

    /**
     * Invoke before test case
     */
    void beforeTest(TestInteraction interaciton);

    /**
     * Invoke after test case
     */
    void afterTest(TestInteraction interaciton);

    /**
     * Before test case tear down
     */
    void beforeTearDown();

    /**
     * After test case tear down
     */
    void afterTearDown();
}
