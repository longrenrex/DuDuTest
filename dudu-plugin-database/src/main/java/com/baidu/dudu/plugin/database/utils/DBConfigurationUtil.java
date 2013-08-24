package com.baidu.dudu.plugin.database.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class DBConfigurationUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(DBConfigurationUtil.class);

	private static XStream xstream = new XStream();

	static {
		xstream.autodetectAnnotations(true);
		xstream.alias("configuration", DBConfiguration.class);
		xstream.alias("database", DBConfigurationInfo.class);
	}

	private static DBConfigurationUtil configurationUtil;

	private DBConfiguration config;

	public DBConfiguration getConfig() {
		return config;
	}

	private static final String dbConfigFile = "db.xml";

	public synchronized static DBConfigurationUtil getInstance() {
		if (configurationUtil == null) {
			configurationUtil = new DBConfigurationUtil();
		}

		return configurationUtil;
	}

	private DBConfigurationUtil() {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(new File(dbConfigFile));
			config = new DBConfiguration();
			xstream.fromXML(fileInputStream, config);
		} catch (Exception e) {
			logger.error("read db configuration file error!", e);
		}

	}

	public static void main(String[] args) {

		try {
			DBConfiguration config = new DBConfiguration();

			// FileOutputStream fileOutputStream = new FileOutputStream(new
			// File("test.xml"));
			// OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
			// fileOutputStream, Charset.forName("utf-8"));
			//
			// DBConfigurationInfo configurationInfo = new
			// DBConfigurationInfo();
			// configurationInfo.setName("name");
			// configurationInfo.setDbType("a");
			// configurationInfo.setUrl("url");
			// configurationInfo.setPassword("bb");
			// config.getDbConfigMap().put("test", configurationInfo);
			// xstream.toXML(config, outputStreamWriter);

			Map<String, DBConfigurationInfo> map = config.getDbConfigMap();
			map.size();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
