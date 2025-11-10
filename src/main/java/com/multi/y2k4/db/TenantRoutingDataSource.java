//package com.multi.y2k4.db;
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import javax.sql.DataSource;
//
////DataSource는 DB와 커넥션 정보를 담고 있는 객체
////AbstractRoutingDataSource는 다양한 DataSource를 담아두고, key로 DataSource를 선택(routing)해서 사용할 수 있음
////DataSourceConfig의 tenantRoutingDataSource에서 실헹
//public class TenantRoutingDataSource extends AbstractRoutingDataSource {
//
//    private final TenantDataSourceProvider dataSourceProvider;
//
//    public TenantRoutingDataSource(TenantDataSourceProvider dataSourceProvider) {
//        this.dataSourceProvider = dataSourceProvider;
//    }
//
//    @Override
//    protected DataSource determineTargetDataSource() {
//        String dbName = TenantContext.getCurrentDb();
//
//        // 로그인 전 등, 테넌트가 정해지지 않은 경우에는 기본 DataSource 사용 (null 처리)
//        if (dbName == null) {
//            return (DataSource) super.determineTargetDataSource(); // defaultTargetDataSource
//        }
//
//        // 현재 요청에서 사용할 DB 이름에 맞는 DataSource를 만든다/가져온다.
//        return dataSourceProvider.getOrCreateDataSource(dbName);
//    }
//
//    /**
//     * 보통은 lookupKey로만 쓰이는데, 여기선 크게 안 사용.
//     * 그래도 null 방지를 위해 override.
//     */
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return TenantContext.getCurrentDb();
//    }
//
//}
