package com.baidu.dudu.plugin.database.message.response;


import com.baidu.dudu.framework.message.DuDuMessage;

public class DuDuCountResp implements DuDuMessage {

    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
