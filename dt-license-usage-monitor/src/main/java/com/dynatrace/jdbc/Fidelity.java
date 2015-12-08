package com.dynatrace.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import oracle.jdbc.pool.OracleDataSource;

public class Fidelity {
	
	public static void main(String[] args) throws Exception {
        String user = System.getProperty("user", "DYNATRACE");
        String password = System.getProperty("pass", "*****");
        String dbhost = System.getProperty("dbhost", "DIPIT-D.FMR.COM");
        String dbport = System.getProperty("dbport", "1521");
        String dbservice = System.getProperty("dbservice", "DIPIT_D");
        String url = "jdbc:oracle:thin:@" + dbhost + ":" + dbport + "/" + dbservice;

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(url);
        ods.setUser(user);
        ods.setPassword(password);
        
        String sql = System.getProperty("sql", "SELECT APP_ID, NAME, COSTCENTER, CCDESC, BUCODE, BUDESC FROM ODVDM.AD_MV_AP_INFO_REGISTERED WHERE APP_ID = 'AP105512'");
        try (Connection conn = ods.getConnection()) {
        	executeStatement(conn, sql);
        }
	}
	
	private static void executeStatement(Connection conn, String sql) throws Exception {
    	Statement stmt = conn.createStatement();
    	// SELECT user FROM dual
    	
    	try (ResultSet resultSet = stmt.executeQuery(sql)) {
    		printResults(resultSet);
    	}
	}
	
	private static void printResults(ResultSet resultSet) throws Exception {
		while (resultSet.next()) {
//			ResultSetMetaData metaData = resultSet.getMetaData();
//			int numColumns = metaData.getColumnCount();
			System.out.println("APP_ID" + ": " + resultSet.getString(1));
			System.out.println("NAME" + ": " + resultSet.getString(2));
			System.out.println("COSTCENTER" + ": " + resultSet.getString(3));
			System.out.println("CCDESC" + ": " + resultSet.getString(4));
			System.out.println("BUCODE" + ": " + resultSet.getString(5));
			System.out.println("BUDESC" + ": " + resultSet.getString(6));
		}
	}

}
