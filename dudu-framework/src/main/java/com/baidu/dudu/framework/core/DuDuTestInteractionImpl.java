package com.baidu.dudu.framework.core;


import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.ComparisonResult;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.ComparisonResult.Difference;
import com.baidu.dudu.framework.plugin.Plugin;

/**
 * @author rzhao
 */
public class DuDuTestInteractionImpl implements TestInteraction {

    private static final Logger logger = LoggerFactory.getLogger(DuDuTestInteractionImpl.class);

    private DuDuTestManager testManager;

    private ConcurrentLinkedQueue<Future<Object>> taskQueue = new ConcurrentLinkedQueue<Future<Object>>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public Plugin getPlugin(String name) throws DuDuException {
        if (name == null) {
            final String errorInfo = "plugin name can't be null!";
            logger.error(errorInfo);
            throw new DuDuException(errorInfo);
        }

        Plugin plugin = testManager.getPlugin(name.toLowerCase());
        plugin.setTestInteraction(this);
        plugin.start();
        return plugin;
    }

    @Override
    public void analyticResult(DuDuMessage message, ComparisonResult result) {
        final StringBuffer errorInfo = new StringBuffer("Comparison result is differenet! " + message.getClass());
        if (!result.getDifferences().isEmpty()) {
            for (Difference difference : result.getDifferences()) {
                errorInfo.append("\r\nProperty:" + difference.getProperty() + "\r\n, expected:" + difference.getExpected() + ", but received:" + difference.getReceived());
            }
            logger.error(errorInfo.toString());
            System.err.println(errorInfo.toString());
            throw new DuDuException(errorInfo.toString());
        }
    }

    /**
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void checkTasks() {
        for (Future<Object> task : taskQueue) {
            try {
                task.get();
            }
            catch (InterruptedException e) {
                final String errorInfo = "check tasks error!";
                logger.error(errorInfo, e);
                throw new DuDuException(errorInfo, e);
            }
            catch (ExecutionException e) {
                final String errorInfo = "check tasks error!";
                logger.error(errorInfo, e);
                throw new DuDuException(errorInfo, e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.dudu.free.framework.interaction.DuDuTestInteraction#afterSetUp()
     */
    @Override
    public void afterSetUp() {
        if (logger.isDebugEnabled()) {
            logger.debug("TestInteraction after setUp.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.dudu.free.framework.interaction.DuDuTestInteraction#afterTearDown()
     */
    @Override
    public void afterTearDown() {
        if (logger.isDebugEnabled()) {
            logger.debug("TestInteraction after tearDown.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.dudu.free.framework.interaction.DuDuTestInteraction#afterTest()
     */
    @Override
    public void afterTest(TestInteraction interaciton) {
        if (logger.isDebugEnabled()) {
            logger.debug("TestInteraction after test.");
        }

        checkTasks();
    }

    /*
     * (non-Javadoc)
     * @see com.dudu.free.framework.interaction.DuDuTestInteraction#beforeSetUp()
     */
    @Override
    public void beforeSetUp() {
        if (logger.isDebugEnabled()) {
            logger.debug("TestInteraction before setUp.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.dudu.free.framework.interaction.DuDuTestInteraction#beforeTearDown()
     */
    @Override
    public void beforeTearDown() {
        if (logger.isDebugEnabled()) {
            logger.debug("TestInteraction before tearDown.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.dudu.free.framework.interaction.DuDuTestInteraction#beforeTest()
     */
    @Override
    public void beforeTest(TestInteraction interaciton) {
        if (logger.isDebugEnabled()) {
            logger.debug("TestInteraction before test.");
        }
    }

    public DuDuTestManager getTestManager() {
        return testManager;
    }

    public void setTestManager(DuDuTestManager testManager) {
        this.testManager = testManager;
    }

    @Override
    public void addTask(Callable<Object> call) {
        Future<Object> task = executorService.submit(call);
        taskQueue.offer(task);
    }
}
