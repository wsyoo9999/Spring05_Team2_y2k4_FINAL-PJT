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
     * [1단계] 회원관리 DB용 DataSource 설정 정보 로딩
     * -------------------------------------------------------
     * - application.yml / application.properties 에 정의된
     *   spring.datasource.management.* 값을 DataSourceProperties 객체에 바인딩한다.
     *
     * 예)
     * spring:
     *   datasource:
     *     management:
     *       url: jdbc:mysql://.../tenant_registry
     *       username: root
     *       password: ...
     *
     * 이렇게 적어둔 값을 가져와서 DataSource를 만들 준비를 한다고 보면 된다.
     */
    @Bean
    @ConfigurationProperties("spring.datasource.management")
    //@ConfigurationProperties는 *.properties , *.yml 파일에 있는 property를 자ataSourceProperties 객체에 자동 바인딩, 사용할 수 있게 해주는 어노테이션
    //해당 부분에선 *.properties , *.yml 파일들 중에서 회원괸리용으로 쓸 값들을 가져온다(디폴트값들,유저들한테 제공할 목적X)
    public DataSourceProperties managementDataSourceProperties() {
        return new DataSourceProperties();  //설정 정보를 담는 객체를 준비
    }

    /**
     * [2단계] 회원관리 DB용 DataSource 생성
     * -------------------------------------------------------
     * 위에서 바인딩된 DataSourceProperties를 이용해서 실제 DB 커넥션 풀(DataSource)을 만든다.
     *
     * - 이 DataSource는 "회원/회사/테넌트 정보" DB에 붙는다.
     * - 로그인, 회사 DB 생성 여부 확인 같은 관리성 작업은 모두 이 DataSource를 통해 실행된다.
     */
    //위의 managementDataSourceProperties()를 통헤 바인딩된, 준비된 객체를 실제 데이터베이스 연결을 담당하는 DataSource 객체를 반환, 이를 Bean으로 만듬
    @Bean(name = "managementDataSource")
    public DataSource managementDataSource() {
        return managementDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * [3단계] 테넌트용 RoutingDataSource 생성
     * -------------------------------------------------------
     * - 이 DataSource는 "실제 DB에 직접 연결"하기보다는,
     *   내부적으로 TenantDataSourceProvider를 사용해서
     *   회사별 DB(acme_db, beta_db, ...)로 라우팅하는 역할을 한다.
     *
     * - defaultTargetDataSource: 아직 테넌트 정보가 없는 경우(로그인 전 등)에 사용할 기본 DS
     *   → 여기서는 관리용 DataSource(회원관리 DB)를 넣었다.
     *
     * - 실제 요청 시 동작 순서 (테넌트 매퍼 기준):
     *   1) TenantContext.getCurrentDb() 로 현재 요청에서 사용할 DB 이름 조회
     *   2) 해당 이름으로 provider.getOrCreateDataSource(dbName) 호출
     *   3) 적절한 HikariDataSource(커넥션 풀)을 선택/생성해서 사용
     */
    @Bean(name = "tenantRoutingDataSource")
    public DataSource tenantRoutingDataSource(TenantDataSourceProvider provider,
                                              @Qualifier("managementDataSource") DataSource defaultDs) {

        // provider를 주입받아서, 내부에서 테넌트별 DataSource를 동적으로 얻어 쓸 수 있게 한다.
        TenantRoutingDataSource ds = new TenantRoutingDataSource(provider);

        // 기본 DS 설정(테넌트 컨텍스트가 없을 때 사용)
        // 아직 로그인 전이나 연결할 회사DB 이름이 없으면 기본값(회원관리 DB)으로 사용
        ds.setDefaultTargetDataSource(defaultDs);

        // AbstractRoutingDataSource 가 요구하는 targetDataSources Map 초기화
        // (우리는 실 구현을 TenantDataSourceProvider에 맡기기 때문에 실제로는 이 Map을 사용하지 않는다)
        ds.setTargetDataSources(new HashMap<>());

        // 내부 설정을 마무리하고 초기화
        ds.afterPropertiesSet();
        return ds;
    }

    /**
     * [4단계] 회원관리용 MyBatis SqlSessionFactory
     * -------------------------------------------------------
     * - "관리 DataSource + 관리용 매퍼 XML" 을 사용하는 MyBatis 공장.
     * - 이 공장으로부터 생성되는 SqlSession 들은 항상 managementDataSource에 붙는다.
     *
     * 정리:
     *  - DataSource : managementDataSource (회원관리 DB)
     *  - Mapper XML : classpath:mapper/management/*.xml
     *  - 용도       : 로그인, 사용자 조회, 회사/DB 정보 관리 등 메타데이터 쿼리
     */

    //SqlSessionFactory란 데이터베이스와의 모든 SQL 작업을 수행하는 SqlSession 인스턴스를 생성한다.
    //SqlSession은 SqlSessionFactory에 의해 생성
    // SqlSessionFactory는 일반적으로 애플리케이션의 생명주기 동안 한번만 생성되고 여러 SqlSession 인스턴스를 생성하는데 사용
    //DataSource는 JDBC에서 직접 DB와 연결해줬던 과정을 MyBatis에선 DataSource 객체가 대신 수행해준다
    //SqlSessionFactory는 SqlSessionFactoryBean 객체를 먼저 형성하고, 이 Bean 객체에 여러가지 설정 값들을 넣은 뒤 해당 설정을 포함하는 SqlSessionFactory를 Bean이 반환하는 형식으로 만들어진다.
    // SqlSessionFactory가 관리용/테넌트용으로 두 개 있기 때문에,
    // @MapperScan(sqlSessionTemplateRef=...)을 사용해서
    // "어떤 패키지의 Mapper들이 어떤 SqlSessionTemplate/SqlSessionFactory를 사용할지"를 명시적으로 구분한다.
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


    //SqlSessionTemplate은 스프링과 마이바티스를 연동할 때 SQL 문을 실행하고 트랜잭션을 관리하는 SqlSession 인터페이스의 구현체
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