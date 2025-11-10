package com.multi.y2k4.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
        basePackages = "com.multi.y2k4.mapper.management",
        sqlSessionTemplateRef = "managementSqlSessionTemplate"
)
public class ManagementMyBatisConfig {
}
