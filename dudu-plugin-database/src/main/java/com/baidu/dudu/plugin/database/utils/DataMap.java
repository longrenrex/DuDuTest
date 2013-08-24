/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */
package com.baidu.dudu.plugin.database.utils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * ��ݼ�¼����map
 * 
 * @author xu.zhaox
 * @version $Id: DateMap.java, v 0.1 2008-3-6 ����02:44:56 xu.zhaox Exp $
 */
public class DataMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 9164967756576939731L;

    /**
     * ȡ��String�������
     * 
     * @param key
     * @return
     */
    public String getStringValue(String key) {
        key = convertKey(key);
        String value = null;
        if (this.containsKey(key)) {
            Object obj = this.get(key);
            if (null == obj) {
                return null;
            } else if (obj instanceof BigDecimal) {
                value = ((BigDecimal) obj).toString();
            } else {
                value = String.valueOf(obj);
            }
        }

        return value;
    }

    /**
     * ȡ��Int������ֵ
     * 
     * @param key
     * @return
     */
    public int getIntValue(String key) {
        key = convertKey(key);
        String stringValue = this.getStringValue(key);
        int value = Integer.parseInt(stringValue);

        return value;
    }

    /**
     * ȡ��Long������ֵ
     * 
     * @param key
     * @return
     */
    public long getLongValue(String key) {
        key = convertKey(key);
        String stringValue = this.getStringValue(key);
        long value = Long.parseLong(stringValue);

        return value;
    }

    /**
     * ��KeyתΪ��д
     * 
     * @param key
     * @return
     */
    public static String convertKey(String key) {
        return key==null||key.length()==0 ? null : key.toUpperCase();
    }
}
