package org.example.app.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Sample021 {
    private static String driver = "com.mysql.cj.jdbc.Driver";

    private static String url = "jdbc:mysql://localhost:3306/mydata?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

    private static String username = "root";

    private static String password = "123456";

    public static Connection getConnetion() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
