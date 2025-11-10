package com.multi.y2k4.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;


//@Configuration은 해당 클래스가 스프링 Bean 설정 클래스임을 의미.
// 따라서 @ComponentScan이 처리될 때 자신뿐만 아니라 이 클래스에 @Bean으로 설정된 모든 Bean들도 초기화
@Configuration
public class DataSourceConfig {

    /**
     * 회원관리 DB용 DataSource 설정 정보 (spring.datasource.management.*)
     */
    @Bean
    @ConfigurationProperties("spring.datasource.management")
    //@ConfigurationProperties는 *.properties , *.yml 파일에 있는 property를 자바 클래스에 값을 가져와서(바인딩) 사용할 수 있게 해주는 어노테이션
    //해당 부분에선 *.properties , *.yml 파일들 중에서 회원괸리용으로 쓸 값들을 가져온다(디폴트값들,유저들한테 제공할 목적X)
    public DataSourceProperties managementDataSourceProperties() {
        return new DataSourceProperties();  //설정 정보를 담는 객체를 준비
    }

    /**
     * 회원관리 DB용 DataSource (로그인, 테넌트 관리 전용)
     */
    //위의 managementDataSourceProperties()를 통헤 바인딩된, 준비된 객체를 실제 데이터베이스 연결을 담당하는 DataSource 객체를 반환, 이를 Bean으로 만듬
    @Bean(name = "managementDataSource")
    public DataSource managementDataSource() {
        return managementDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * 테넌트용 RoutingDataSource
     * - defaultTargetDataSource: 일단 managementDataSource 사용(로그인 전 등)
     * - 실제 쿼리 시 TenantRoutingDataSource가 TenantDataSourceProvider를 사용해
     *   적절한 테넌트 DB로 연결한다.
     */
    @Bean(name = "tenantRoutingDataSource")
    public DataSource tenantRoutingDataSource(TenantDataSourceProvider provider,
                                              @Qualifier("managementDataSource") DataSource defaultDs) {
        TenantRoutingDataSource ds = new TenantRoutingDataSource(provider);

        // 기본 DS 설정(테넌트 컨텍스트가 없을 때 사용)
        ds.setDefaultTargetDataSource(defaultDs);   //아직 로그인 전이나 연결할 회사DB 이름이 없으면 기본값(회원관리 DB)으로 설정
        ds.setTargetDataSources(new HashMap<>()); // 필수 초기화 (실제론 사용 안 함)

        ds.afterPropertiesSet();
        return ds;
    }

    /**
     * 회원관리용 MyBatis SqlSessionFactory
     * (관리 DataSource + 관리용 매퍼들)
     */
    @Bean(name = "managementSqlSessionFactory")
    public SqlSessionFactory managementSqlSessionFactory(
            @Qualifier("managementDataSource") DataSource ds) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(ds);
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath:mapper/management/*.xml")
        );
        return factoryBean.getObject();
    }   //

    @Bean(name = "managementSqlSessionTemplate")
    public SqlSessionTemplate managementSqlSessionTemplate(
            @Qualifier("managementSqlSessionFactory") SqlSessionFactory sf) {
        return new SqlSessionTemplate(sf);
    }

    /**
     * 테넌트용 MyBatis SqlSessionFactory
     * (RoutingDataSource + 테넌트용 매퍼들)
     */
    @Bean(name = "tenantSqlSessionFactory")
    public SqlSessionFactory tenantSqlSessionFactory(
            @Qualifier("tenantRoutingDataSource") DataSource ds) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(ds);
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath:mapper/tenant/*.xml")
        );
        return factoryBean.getObject();
    }

    @Bean(name = "tenantSqlSessionTemplate")
    public SqlSessionTemplate tenantSqlSessionTemplate(
            @Qualifier("tenantSqlSessionFactory") SqlSessionFactory sf) {
        return new SqlSessionTemplate(sf);
    }

}