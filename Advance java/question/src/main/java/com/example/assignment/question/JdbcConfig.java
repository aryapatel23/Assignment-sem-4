package com.example.assignment.question;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * JDBC Configuration for airline database (Q1)
 * Uses HikariCP connection pooling for better performance and resource management
 */
@Configuration
public class JdbcConfig {

    /**
     * Configure HikariCP DataSource for airline database
     * HikariCP is the fastest and most reliable connection pool for Java
     */
    @Bean(name = "airlineDataSource")
    public DataSource airlineDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/airlinedb");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(600000);
        config.setConnectionTimeout(20000);
        config.setAutoCommit(false); // Important for transaction management
        
        return new HikariDataSource(config);
    }
}
