package com.multi.y2k4.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;


//Mapper를 주입하는 역학
//여러개의 DataSource를 사용하므로 자동 주입이 잘 작동이 안될 수 있으르모 이를 직접 설정
@Configuration
@MapperScan(
        basePackages = "com.multi.y2k4.mapper.management",
        sqlSessionTemplateRef = "managementSqlSessionTemplate"
)

//base-package 속성은 매퍼 인터페이스 파일이 있는 가장 상위 패키지를 지정하면 된다. 세미콜론이나 콤마를 구분자로 사용해서 한 개 이상의 패키지를 셋팅할 수 있다. 매퍼는 지정된 패키지에서 재귀적으로 하위 패키지를 모두 검색할 것이다.
public class ManagementMyBatisConfig {
}
