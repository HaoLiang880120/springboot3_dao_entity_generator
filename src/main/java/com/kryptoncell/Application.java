package com.kryptoncell;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        /* 将项目时区设置为UTC */
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        var application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.LOG);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setHeadless(Boolean.FALSE);
        application.run(args);
    }

}
