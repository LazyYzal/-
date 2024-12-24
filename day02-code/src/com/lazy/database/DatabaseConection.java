package com.lazy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConection {
   private static final String DB_URL = "jdbc:mysql://localhost:3306/commerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "123456";


   public static Connection getConnection() throws SQLException {
      return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
   }

}
