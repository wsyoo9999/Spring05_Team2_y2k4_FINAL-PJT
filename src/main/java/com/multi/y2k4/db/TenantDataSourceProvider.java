package com.multi.y2k4.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
//해당 클래스를 자동으로 빈으로 만듦(기본적으론 싱글톤)
public class TenantDataSourceProvider {

    // DB 이름 → DataSource 매핑 캐시
    private final ConcurrentMap<String, DataSource> cache = new ConcurrentHashMap<>();

    // application.yml에서 주입
    @Value("${app.mysql.host}")
    private String host;

    @Value("${app.mysql.port}")
    private int port;

    @Value("${app.mysql.username}")
    private String username;

    @Value("${app.mysql.password}")
    private String password;

    @Value("${app.mysql.urlPattern}")
    private String urlPattern;
    // 예: jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=utf8

    /**
     * dbName에 해당하는 DataSource를 가져오거나, 없으면 새로 생성.
     */
    public DataSource getOrCreateDataSource(String dbName) {
        return cache.computeIfAbsent(dbName, this::createDataSource);
    }

    /**
     * 실제 HikariDataSource 생성 로직
     */
    private DataSource createDataSource(String dbName) {
        String jdbcUrl = String.format(urlPattern, host, port, dbName);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setIdleTimeout(600_000);   // 10분
        config.setMaxLifetime(1_800_000); // 30분
        config.setConnectionTimeout(30_000);
        config.setPoolName("tenant-pool-" + dbName);

        return new HikariDataSource(config);
    }
}