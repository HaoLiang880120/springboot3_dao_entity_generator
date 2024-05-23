package com.kryptoncell.gen_db;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.Objects.isNull;

@Repository
public class InformationSchemaDao {

    private final JdbcClient jdbcClient;

    public InformationSchemaDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<String> getTables(String dbName, int size, int offset) {
        var sql = """
                SELECT TABLE_NAME
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = :dbName
                LIMIT :offset, :size
                """;
        return this.jdbcClient
                .sql(sql)
                .param("dbName", dbName)
                .param("size", size)
                .param("offset", offset)
                .query(String.class)
                .list();
    }

    public int getAllTableCount(String dbName) {
        var sql = """
                SELECT COUNT(*) AS TABLE_COUNT
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = :dbName
                """;

        return this.jdbcClient
                .sql(sql)
                .param("dbName", dbName)
                .query(Integer.class)
                .single();
    }

    public List<String> getTables(String dbName, Collection<String> wantTables) {

        if (isNull(wantTables) || wantTables.isEmpty()) {
            return Collections.emptyList();
        }

        var sql = """
                SELECT TABLE_NAME
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = :dbName AND TABLE_NAME IN (:tables)
                """;

        return this.jdbcClient
                .sql(sql)
                .param("dbName", dbName)
                .param("tables", wantTables)
                .query(String.class)
                .list();
    }

    public Map<String, List<TableColumnMetadata>> getColumns(String dbName, Collection<String> tables) {

        var rtnMap = new HashMap<String, List<TableColumnMetadata>>();

        var sql = """
                SELECT
                    TABLE_NAME,
                    COLUMN_NAME,
                    IS_NULLABLE,
                    DATA_TYPE,
                    COLUMN_COMMENT,
                    ORDINAL_POSITION
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = :dbName AND TABLE_NAME IN (:tables)
                """;

        this.jdbcClient
                .sql(sql)
                .param("dbName", dbName)
                .param("tables", tables)
                .query(rs -> {
                    var tableName = rs.getString("TABLE_NAME");
                    var columnName = rs.getString("COLUMN_NAME");
                    var isNullable = "YES".equalsIgnoreCase(rs.getString("IS_NULLABLE"));
                    var dataType = rs.getString("DATA_TYPE");
                    var comment = rs.getString("COLUMN_COMMENT");
                    // 列顺序
                    var ordinalPosition = rs.getInt("ORDINAL_POSITION");

                    var columnMetaData = new TableColumnMetadata(
                            columnName, isNullable, dataType, comment, ordinalPosition
                    );

                    var columnList = rtnMap.get(tableName);
                    if (isNull(columnList)) {
                        columnList = new ArrayList<>();
                        rtnMap.put(tableName, columnList);
                    }

                    columnList.add(columnMetaData);
                });

        return rtnMap;
    }

}
