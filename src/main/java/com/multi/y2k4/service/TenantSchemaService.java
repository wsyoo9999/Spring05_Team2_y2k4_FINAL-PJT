package com.multi.y2k4.service;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantSchemaService {
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

    public void migrate(String dbName) {
        // 1) 해당 테넌트 DB에 대한 JDBC URL 조립
        String url = String.format(urlPattern, host, port, dbName);

        // 2) Flyway 인스턴스 구성
        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)    // 이 DB에 접속
                .locations("classpath:db/tenant")       // 우리가 만든 V1__base_schema.sql 위치
                .baselineOnMigrate(true)                // 기존 DB에 적용 시에도 V1부터 시작하도록
                .load();

        // 3) 마이그레이션 실행 (V1__... → V2__..., 순서대로)
        flyway.migrate();

    }
}
