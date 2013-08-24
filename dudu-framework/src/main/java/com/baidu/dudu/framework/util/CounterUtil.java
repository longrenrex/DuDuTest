package com.baidu.dudu.framework.util;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rzhao
 */
public class CounterUtil {

	private static Logger logger = LoggerFactory.getLogger(CounterUtil.class);

	private static CounterUtil instance;

	private AtomicLong counter;

	public static synchronized CounterUtil getInstance() {
		if (instance == null) {
			instance = new CounterUtil();
		}
		return instance;
	}

	private CounterUtil() {
		counter = new AtomicLong();
	}

	public void setCounter(int i) {
		this.counter = new AtomicLong(i);
	}

	public long getNumber() {
		return counter.getAndIncrement();
	}

}
