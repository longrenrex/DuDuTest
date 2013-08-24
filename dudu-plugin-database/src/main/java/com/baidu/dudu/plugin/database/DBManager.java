package com.baidu.dudu.plugin.database;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.plugin.database.utils.DBConfiguration;
import com.baidu.dudu.plugin.database.utils.DBConfigurationInfo;
import com.baidu.dudu.plugin.database.utils.DBConfigurationUtil;

public class DBManager {

	private static final Logger logger = LoggerFactory
			.getLogger(DBManager.class);

	private static Connection getConnection(String DBType, String url,
			String user, String password) {
		String jdbcDriver;
		if (DBType.equalsIgnoreCase("mysql")) {
			jdbcDriver = "com.mysql.jdbc.Driver";
		} else {
			jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		}
		logger.debug("JDBC driver: {}", jdbcDriver);
		try {
			Class.forName(jdbcDriver);
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	public static void releaseConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
				logger.debug("DB connection close.");
			} catch (Exception e) {
				logger.error("DB connection close error!", e);
			}
		}
	}

	public static Connection connectDb(String dbName) {
		logger.debug("connect to db, name={}", dbName);
		DBConfiguration config = DBConfigurationUtil.getInstance().getConfig();
		DBConfigurationInfo configurationInfo = config.getDbConfigMap().get(
				dbName);
		return DBManager.getConnection(configurationInfo.getDbType(),
				configurationInfo.getUrl(), configurationInfo.getUsername(),
				configurationInfo.getPassword());
	}
}
