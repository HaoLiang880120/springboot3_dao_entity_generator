package individual.hl_so2.utils;

import org.apache.commons.cli.*;

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

        // 生成的java的包名
        var genBaseJavaPackage = Option.builder("gen-base-package")
                .longOpt("gen-base-java-package")
                .desc("生成的代码的根包名")
                .required(true)
                .optionalArg(false)
                .hasArg(true)
                .numberOfArgs(1)
                .type(String.class)
                .build();

        var genRemainTablePrefix = Option.builder("gen-remain-table-prefix")
                .longOpt("gen-entity-name-remain-table-prefix")
                .desc("生成的entity名是否保留表前缀")
                .required(true)
                .optionalArg(false)
                .hasArg(true)
                .numberOfArgs(1)
                .type(String.class)
                .build();

        var genOutputDir = Option.builder("gen-output-dir")
                .longOpt("gen-output-dir")
                .desc("生成代码到哪个目录")
                .required(true)
                .optionalArg(false)
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
                .addOption(dbTables)
                .addOption(genBaseJavaPackage)
                .addOption(genRemainTablePrefix)
                .addOption(genOutputDir);
    }

    public static void printHelp(Options options) {
        new HelpFormatter().printHelp(
                1000,
                "java -jar xxx.jar -host 127.0.0.1 -port 3306 -user db_user_name [-password 123445sf] -database my_database [-tables t_user,t_image] -gen-base-package com.package.name -gen-remain-table-prefix true -gen-output-dir /Users/Tao123/Downloads/",
                "\n",
                options,
                "\n"
        );
    }

}
