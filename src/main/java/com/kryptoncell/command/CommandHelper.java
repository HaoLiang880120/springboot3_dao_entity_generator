package com.kryptoncell.command;

import com.kryptoncell.db.DBFetcher;
import com.kryptoncell.utils.JDBCHelper;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CommandHelper {

    public static Options buildOptions() {

        // 数据库地址
        var dbHost = Option.builder("host")
                .longOpt("database-host")
                .desc("数据库域名/ip")
                .required(true)
                .hasArg(true)
                .numberOfArgs(1)
                .type(String.class)
                .build();

        // 数据库端口
        var dbPort = Option.builder("port")
                .longOpt("database-port")
                .desc("数据库端口号")
                .required(true)
                .hasArg(true)
                .numberOfArgs(1)
                .type(Integer.class)
                .build();

        // 数据库名
        var dbName = Option.builder("database")
                .longOpt("database-name")
                .desc("想要生成dao、entity的表所在的数据库名")
                .required(true)
                .hasArg(true)
                .numberOfArgs(1)
                .type(String.class)
                .build();

        // 登陆的数据库用户
        var dbUser = Option.builder("user")
                .longOpt("database-user")
                .desc("数据库登陆用户名")
                .required(true)
                .hasArg(true)
                .numberOfArgs(1)
                .type(String.class)
                .build();

        // 登陆的数据库用户密码
        var dbPassword = Option.builder("password")
                .longOpt("database-password")
                .desc("数据库登陆密码")
                .required(false)
                .optionalArg(true)
                .hasArg(true)
                .numberOfArgs(1)
                .type(String.class)
                .build();

        // 要生成代码的表
        var dbTables = Option.builder("tables")
                .longOpt("database-tables")
                .desc("想要生成对应entity、dao的表，用英文逗号','分隔。不设置则生成所有表对应的代码")
                .required(false)
                .optionalArg(true)
                .hasArg(true)
                .numberOfArgs(1)
                .type(String.class)
                .build();

        return new Options()
                .addOption(dbHost)
                .addOption(dbPort)
                .addOption(dbName)
                .addOption(dbUser)
                .addOption(dbPassword)
                .addOption(dbTables);
    }

    public static void parseCommand(Options options, String[] args) {

        var parser = new DefaultParser();

        try {
            var cmd = parser.parse(options, args);

            var dbHost = cmd.getOptionValue("host");
            var dbPort = Integer.parseInt(cmd.getOptionValue("port"));
            var dbName = cmd.getOptionValue("database");
            var dbUser = cmd.getOptionValue("user");
            var dbPassword = cmd.getOptionValue("password");

            // 需要生成代码的表，如果-tables选项没有设置，则置为空列表
            List<String> tables = Collections.emptyList();
            if (cmd.hasOption("tables")) {
                tables = Arrays.stream(cmd.getOptionValue("tables").split(",")).toList();
            }

            // 将数据库相关参数赋值给JDBCHelper
            JDBCHelper.dbHost = dbHost;
            JDBCHelper.dbPort = dbPort;
            JDBCHelper.dbName = dbName;
            JDBCHelper.dbUser = dbUser;
            JDBCHelper.dbPassword = dbPassword;

            // 单独校验想要生成代码的表，校验通过则将要生成的表赋值给JDBCHelper
            var allTables = DBFetcher.getAllTableNames();
            if (tables.isEmpty()) {
                JDBCHelper.tables = allTables;
            } else {
                DBFetcher.verifyTableExists(allTables, tables);
                JDBCHelper.tables = tables;
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printHelp(Options options) {
        new HelpFormatter().printHelp(
                1000,
                "java -jar xxx.jar -host 127.0.0.1 -port 3306 -user db_user_name [-password 123445sf] - -database my_database [-tables t_user,t_image]",
                "\n",
                options,
                "\n"
        );
    }

}
