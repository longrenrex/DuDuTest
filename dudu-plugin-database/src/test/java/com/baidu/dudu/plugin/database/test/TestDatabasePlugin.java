package com.baidu.dudu.plugin.database.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.DuDuTest;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.framework.plugin.Plugin;
import com.baidu.dudu.plugin.database.DBManager;
import com.baidu.dudu.plugin.database.message.request.DuDuCountReq;
import com.baidu.dudu.plugin.database.message.request.DuDuQueryReq;
import com.baidu.dudu.plugin.database.message.response.DuDuCountResp;
import com.baidu.dudu.plugin.database.message.response.DuDuQueryResp;
import com.baidu.dudu.plugin.database.utils.DataMap;

public class TestDatabasePlugin extends DuDuTest {

	private static final Logger logger = LoggerFactory.getLogger(TestDatabasePlugin.class);

	Plugin dbClient;

	@Override
	public void setUp(TestInteraction testInteraction) {
		logger.info("TestDatabasePlugin setUp");
		dbClient = testInteraction.getPlugin("database");
	}

	@Override
	public void testStart(TestInteraction testInteraction) {
//		 DuDuCountReq duduCountReq = new DuDuCountReq();
//		 duduCountReq.setSql("select count(*) from city");
//		 dbClient.send(duduCountReq);
//		
//		 DuDuCountResp resp = new DuDuCountResp();
//		 resp.setCount(Integer.valueOf(600));
//		 dbClient.receive(resp);
//		
//		 DuDuQueryReq duduQueryReq = new DuDuQueryReq();
//		 duduQueryReq.setSql("select * from city");
//		 dbClient.send(duduQueryReq);
//		
//		 DuDuSessionMessage sessionMessage = dbClient.receive();
//		 DuDuQueryResp duduQueryResp = (DuDuQueryResp)
//		 sessionMessage.getMessage();
//		 List<Map<String, Object>> list = duduQueryResp.getList();
//		
//		 System.err.println(list.size());
//		 Map<String, Object> map = list.get(0);
//		 System.err.println(map.get("CITY"));

		Connection dbConnnection = DBManager.connectDb("testdb02");

		List<DataMap> dataList = new ArrayList<DataMap>();
		try {
			PreparedStatement stmt = dbConnnection.prepareStatement("select * from city");
			ResultSet resultSet = stmt.executeQuery();
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			while (resultSet.next()) {
				DataMap map = new DataMap();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					map.put(resultSetMetaData.getColumnName(i).toUpperCase(), resultSet.getObject(i));
				}
				logger.info("map size = {}", map.size());
				dataList.add(map);
			}

			logger.info("dataList size = {}", dataList.size());

		} catch (SQLException e) {
			logger.error("DB operation error!", e);
		} finally {
			DBManager.releaseConnection(dbConnnection);
		}
	}

	@Override
	public void tearDown(TestInteraction testInteraction) {
		// TODO Auto-generated method stub

	}

}
