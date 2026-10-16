package com.finguard.util;
import java.sql.*;
public final class DatabaseConnection {
 private DatabaseConnection(){}
 public static boolean configured(){return System.getenv("DB_URL")!=null&&!System.getenv("DB_URL").isBlank();}
 public static Connection open() throws SQLException {String url=System.getenv("DB_URL");if(url==null||url.isBlank())throw new SQLException("MySQL is not configured. Set DB_URL, DB_USER and DB_PASSWORD after creating the database with schema.sql.");return DriverManager.getConnection(url,System.getenv().getOrDefault("DB_USER","root"),System.getenv().getOrDefault("DB_PASSWORD",""));}
}
