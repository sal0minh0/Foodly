package com.foodly.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    // Conexão com o MYSQL SERVER
    private static final String LOCALHOST_URL = "jdbc:mysql://localhost:3306/foodly?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String EC2_URL = "jdbc:mysql://ec2-52-15-171-120.us-east-2.compute.amazonaws.com:3306/foodly?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "user";
    private static final String PASSWORD = "";
    
    private static String getURL() {
        String env = System.getenv("ENVIRONMENT");
        if ("production".equalsIgnoreCase(env)) {
            return EC2_URL;
        }
        return LOCALHOST_URL;
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(getURL(), USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do MySQL não foi encontrado", e);
        }
    }
}
