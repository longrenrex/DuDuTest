package com.baidu.dudu.plugin.database.message.request;

import com.baidu.dudu.framework.message.DuDuMessage;


public class DuDuExcuteReq implements DuDuMessage {

    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
