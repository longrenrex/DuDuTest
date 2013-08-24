package com.baidu.dudu.plugin.database.utils;

import java.util.HashMap;
import java.util.Map;

public class DBConfiguration {

	private Map<String, DBConfigurationInfo> dbConfigMap = new HashMap<String, DBConfigurationInfo>();

	public Map<String, DBConfigurationInfo> getDbConfigMap() {
		return dbConfigMap;
	}

	public void setDbConfigMap(Map<String, DBConfigurationInfo> dbConfigMap) {
		this.dbConfigMap = dbConfigMap;
	}

}
