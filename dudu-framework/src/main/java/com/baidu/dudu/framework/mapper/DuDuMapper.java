package com.baidu.dudu.framework.mapper;


import com.baidu.dudu.framework.message.ComparisonResult;
import com.baidu.dudu.framework.message.DuDuMessage;

/**
 * @author rzhao
 */
public interface DuDuMapper {

    /**
     * Compare the message from testcase to the message from plugin
     * 
     * @param contextFromTestCase
     * @param contextFromPlugin
     * @return
     */
    public ComparisonResult compare(DuDuMessage contextFromTestCase, DuDuMessage contextFromPlugin);
}
