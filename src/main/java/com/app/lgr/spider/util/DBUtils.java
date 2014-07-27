package com.app.lgr.spider.util;

import java.sql.*;

public class DBUtils {
	private static String className = "";
	private static String url = "";
	private static String user = "";
	private static String password = "";
	static {
		className = DynamicConfig.getStr("className");
		url = DynamicConfig.getStr("url");
		user = DynamicConfig.getStr("user");
		password = DynamicConfig.getStr("password");
	}
	/**
	 * 获取数据库连接
	 * @return Connection
	 * @throws SQLException, ClassNotFoundException
	 */
	public static Connection getConn() throws SQLException, ClassNotFoundException {
		Class.forName(className);
        return DriverManager.getConnection(url, user, password);
	}
	
	/**
	 * 关闭操作数据库的相关资源
	 * @param conn Connection
	 * @param stmt Statement
	 * @param rs ResultSet
	 * @throws SQLException
	 */
	public static void close(Connection conn, Statement stmt, ResultSet rs) throws SQLException{
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		if (conn != null) {
			conn.close();
		}
	}

    public static void main(String[] args) throws Exception {
        Connection conn = DBUtils.getConn();
        System.out.println(conn);
    }

}
