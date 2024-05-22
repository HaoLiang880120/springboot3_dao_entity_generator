package com.kryptoncell.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

    @Bean
    public DataSource dataSource(@Value("${generate.db.host}") String dbHost,
                                 @Value("${generate.db.port}") String dbPort,
                                 @Value("${generate.db.user}") String dbUsername,
                                 @Value("${generate.db.password}") String dbPassword) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setAutoCommit(Boolean.TRUE);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setMaximumPoolSize(1);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setPoolName("HikariCP");
        hikariConfig.setUsername(dbUsername);
        hikariConfig.setPassword(dbPassword);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/information_schema");

        return new HikariDataSource(hikariConfig);
    }

}
