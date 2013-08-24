package com.baidu.dudu.framework.interaction;


/**
 * @author rzhao
 */
public enum TestPhase {

    Unknown("Unknown"), SetUp("SetUp"), Execute("Execute"), TearDown("TearDown");

    private String name;

    private TestPhase(String name) {
        this.name = name;
    }

    public String toString() {
        return "TestInteraction testPhase :" + name;
    }
}