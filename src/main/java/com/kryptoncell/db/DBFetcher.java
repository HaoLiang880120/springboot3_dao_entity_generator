package com.kryptoncell.db;

import com.kryptoncell.utils.JDBCHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DBFetcher {

    public static int getTableCount() {
        var connection = JDBCHelper.getConnection();

        /* 查询数据库有多少张表 */
        var tableCountSql = """
                SELECT COUNT(*) AS TABLE_COUNT
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = ?;
                """;

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(tableCountSql);
            preparedStatement.setString(1, JDBCHelper.dbName);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();
            return resultSet.getInt("TABLE_COUNT");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBCHelper.closeResultSet(resultSet);
            JDBCHelper.closeStatement(preparedStatement);
        }
    }

    public static List<String> getAllTableNames() {

        var tableCount = getTableCount();
        if (0 == tableCount) {
            return Collections.emptyList();
        }

        /* 如果数据库有海量的表，则每次最多查询100张表。通过 limit x, batchSize 语法来查 */
        int batchSize = 100;
        // 获取多个 limit x, batchSize 中x的值
        var xList = new ArrayList<Integer>();
        for (var x = 0; x < tableCount; x++) {
            if (0 != x % batchSize) {
                continue;
            }

            xList.add(x);
        }

        // 拼接多个查询sql
        var sql = """
                    SELECT TABLE_NAME
                    FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = '%s'
                    LIMIT %d, %d
                    """;
        var sqlList = xList.stream()
                .map(x -> String.format(sql, JDBCHelper.dbName, x, batchSize))
                .toList();

        // 执行这些sql，并将表全部放入一个list中
        var tableList = new ArrayList<String>();

        var connection = JDBCHelper.getConnection();
        sqlList.forEach(eachSql -> {
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(eachSql);

                while (resultSet.next()) {
                    tableList.add(resultSet.getString("TABLE_NAME"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                JDBCHelper.closeResultSet(resultSet);
                JDBCHelper.closeStatement(statement);
            }
        });

        return tableList;
    }

    public static void verifyTableExists(List<String> allTablesNames, List<String> wantedTableNames) {
        for (String tableName : wantedTableNames) {
            if (!allTablesNames.contains(tableName)) {
                throw new RuntimeException("Table `" + tableName + "` does not exist.");
            }
        }
    }

}
