package com.kryptoncell.utils;

import java.sql.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class JDBCHelper {

    // 全局仅使用一个connection
    private static Connection connection;

    // 获取connection
    public static Connection getConnection(String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) {
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

        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        connection = null;
    }

}
