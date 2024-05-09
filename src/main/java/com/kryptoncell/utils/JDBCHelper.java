package com.kryptoncell.utils;

import java.sql.*;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class JDBCHelper {
    public static String dbHost;
    public static int dbPort;
    public static String dbName;
    public static String dbUser;
    public static String dbPassword;
    public static List<String> tables;

    // 全局仅使用一个connection
    private static Connection connection;

    // 获取connection
    public static Connection getConnection() {
        if (nonNull(connection)) {
            return connection;
        }

        synchronized (JDBCHelper.class) {
            if (nonNull(connection)) {
                return connection;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                var url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
                return DriverManager.getConnection(url, dbUser, dbPassword);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (isNull(resultSet)) {
            return;
        }

        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeStatement(Statement statement) {
        if (isNull(statement)) {
            return;
        }

        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        if (isNull(connection)) {
            return;
        }

        synchronized (JDBCHelper.class) {
            if (isNull(connection)) {
                return;
            }

            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            connection = null;
        }

    }

    public static String toPrintString() {
        return "JDBCHelper{" +
                "tables=" + tables +
                ", dbPassword='" + dbPassword + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbPort=" + dbPort +
                ", dbHost='" + dbHost + '\'' +
                '}';
    }
}
