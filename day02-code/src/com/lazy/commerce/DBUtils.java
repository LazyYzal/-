package com.lazy.commerce;

import java.sql.*;

public class DBUtils {
    // 数据库连接信息
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/commerce?" +
            "useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // 注册驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 关闭资源：
    public static void close(Connection connection, Statement statement) throws SQLException{
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }
    public static void close(Connection c, Statement s, ResultSet r) throws SQLException {
        if (r != null) r.close();
        if (s != null) s.close();
        if (c != null) c.close();
    }

}
