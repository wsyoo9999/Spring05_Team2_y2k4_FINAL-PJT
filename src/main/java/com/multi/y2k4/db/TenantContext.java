package com.multi.y2k4.db;

public class TenantContext {

    /**
     * 현재 스레드(요청)에서 사용할 DB 이름을 저장하는 ThreadLocal.
     * ex) "acme_db", "beta_db"
     */

    //스프링 프레임워크는 1개의 API요청은 하나의 스레드가 처리함, 각 스레드별로 데이터를 담아둘 수 있는 공간이 스레드로컬
    //스레드가 살아있는 한 같은 스레드 내에서 공유 가능한 데이터 목록
    private static final ThreadLocal<String> CURRENT_DB = new ThreadLocal<>();

    /**
     * 현재 요청에서 사용할 DB 이름 설정
     */
    public static void setCurrentDb(String dbName) {
        CURRENT_DB.set(dbName);
    }

    /**
     * 현재 요청에서 사용할 DB 이름 조회
     */
    public static String getCurrentDb() {
        return CURRENT_DB.get();
    }

    /**
     * 요청 종료 시 반드시 제거 (메모리 누수 방지)
     */
    public static void clear() {
        CURRENT_DB.remove();
    }
}