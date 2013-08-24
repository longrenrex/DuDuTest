package com.baidu.dudu.plugin.database.message.response;


import java.util.List;
import java.util.Map;

import com.baidu.dudu.framework.message.DuDuMessage;

public class DuDuQueryResp implements DuDuMessage {

    private List<Map<String, Object>> list;

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

}
