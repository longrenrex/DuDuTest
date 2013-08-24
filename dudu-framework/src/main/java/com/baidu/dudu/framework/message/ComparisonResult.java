package com.baidu.dudu.framework.message;


import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author rzhao
 */
public class ComparisonResult {

    private ConcurrentLinkedQueue<Difference> differences = new ConcurrentLinkedQueue<Difference>();

    private Object NULL = new Object();

    public void addDifference(String property, Object expected, Object received) {
        differences.add(new Difference(property, expected, received));
    }

    public void addExpectedDifference(String property, Object expected) {
        differences.add(new Difference(property, expected, NULL));
    }

    public void addReceivedDifference(String property, Object received) {
        differences.add(new Difference(property, NULL, received));
    }

    public ConcurrentLinkedQueue<Difference> getDifferences() {
        return differences;
    }

    public void setDifferences(ConcurrentLinkedQueue<Difference> differences) {
        this.differences = differences;
    }

    public static class Difference {

        private String property;

        private Object expected;

        private Object received;

        public Difference(String property, Object expected, Object received) {
            this.property = property;
            this.expected = expected;
            this.received = received;
        }

        public String getProperty() {
            return property;
        }

        public Object getExpected() {
            return expected;
        }

        public void setExpected(Object expected) {
            this.expected = expected;
        }

        public Object getReceived() {
            return received;
        }

        public void setReceived(Object received) {
            this.received = received;
        }
    }
}
