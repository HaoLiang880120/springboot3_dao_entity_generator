package com.kryptoncell;

import com.kryptoncell.utils.CommandHelper;
import org.apache.commons.cli.DefaultParser;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        /* 将项目时区设置为UTC */
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        /* 解析命令行 */
        var options = CommandHelper.buildOptions();
        CommandHelper.printHelp(options);

        var cmdParser = new DefaultParser();
        var cmd = cmdParser.parse(options, args);

        var genArgs = new String[]{
                "--generate.db.host=" + cmd.getOptionValue("host"),
                "--generate.db.port=" + Integer.parseInt(cmd.getOptionValue("port")),
                "--generate.db.user=" + cmd.getOptionValue("user"),
                "--generate.db.password=" + cmd.getOptionValue("password"),
                "--generate.db.name=" + cmd.getOptionValue("database"),
                "--generate.db.tables=" + cmd.getOptionValue("tables")
        };

        /* 启动项目 */
        var application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.LOG);
        application.run(genArgs);
    }

}
