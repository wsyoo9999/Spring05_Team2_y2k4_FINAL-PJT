# Y2K4 ERP - 컨테이너 기반 커스터마이징 전자결재 시스템

> **기업의 핵심 업무(인사, 재무, 생산, 재고, 영업)를 통합 관리하며, 모든 데이터 조작에 전자결재 프로세스를 도입하여 업무 투명성과 효율성을 극대화한 웹 기반 ERP 시스템입니다.**

---

## 1. 프로젝트 개요

* **프로젝트명**: Y2K4 ERP
* **팀명**: Y2K4
* **개발 기간**: 2025.10.27 ~ 2025.12.02
* **주요 목표**:

  * **전자결재 기반 프로세스**: 데이터의 생성/수정/삭제(CRUD) 시 즉시 반영되지 않고 결재 승인 과정을 거쳐 DB에 반영되는 워크플로우 구현.
  * **멀티 테넌시(Multi-tenancy)**: 가입 시 회사별로 독립적인 Database(`db_{사업자번호}`)를 동적으로 생성 및 라우팅하여 데이터 격리 및 커스터마이징 용이성 확보.
  * **실시간 알림**: SSE(Server-Sent Events)를 활용한 결재 요청 및 처리 결과 실시간 알림.

## 2. 기술 스택

### Backend

* **Language**: Java 17
* **Framework**: Spring Boot 3.x
* **Database Access**: MyBatis 3.0.5
* **Database**: MySQL 8.x (Multi-tenancy architecture)
* **Security**: Spring Security (BCrypt 암호화, Custom Authentication)
* **Build Tool**: Gradle

### Frontend

* **Template Engine**: Thymeleaf
* **Language**: JavaScript (ES6+), jQuery
* **CSS**: Custom CSS, FontAwesome
* **Charting**: Chart.js (대시보드)

### Infrastructure & Tools

* **Server**: AWS EC2 (Ubuntu) - *Development Environment*
* **Web Server**: Nginx (Reverse Proxy)
* **Container**: Docker (MySQL 컨테이너 활용)
* **Collaboration**: Git, GitHub
* **API**: Bizno API (사업자 번호 조회)

## 3. 주요 기능

### 대시보드

* 금월/전월 매출 비교 KPI 카드.
* 최근 1년 월별 매출 추이 (Line Chart).
* 판매/구매 TOP 5 품목 현황 (Doughnut Chart).

### 재무/회계 (Finance)

* **수익(Profit) / 지출(Spend)**: 매출 및 매입, 급여 지급 등에 따른 자동 회계 처리.
* **미수금/미지급금(Unpaid)**: 거래 발생 시 미정산 내역 자동 생성 및 정산 처리.

### 판매/구매 (Transaction)

* **판매(Sale)**: 주문 접수, 납기일 관리, 재고 연동.
* **구매(Purchase)**: 원자재 발주, 입고 예정일 관리.

### 생산/제조 (Production)

* **작업지시서(Work Order)**: 생산 계획 등록 및 상태 관리 (대기 → 진행중 → 완료/폐기).
* **BOM(자재명세서)**: 완제품 생산에 필요한 원자재 소요량 정의.
* **생산 실적(Lot) 및 불량 관리**: Lot 단위 생산 실적 등록 시 원자재 재고 자동 차감 및 불량 수량 기록.

### 재고 (Stock)

* **입고(Inbound) / 출고(Outbound)**: 자재 및 상품의 입출고 이력 관리.
* **재고(Stock) 현황**: 실재고(Qty)와 가용재고/요청수량(Acquired Qty) 분리 관리.
* **연동**: 생산 실적 등록 시 자재 자동 출고, 판매 승인 시 상품 출고 예약.

### 인사 (HR)

* **사원 관리**: 직급(사원, 중간관리자, 최고관리자)에 따른 권한 부여.
* **근태 관리**: 일일 근태 자동 생성, 휴가 신청 및 결재.
* **급여 관리**: 기본급, 수당, 공제액을 포함한 급여 대장 관리.

### 마이페이지 (My Page)

* **개인 정보 조회**: 이름, 부서, 직급, 연락처 등 기본 정보 및 소속 회사 정보 조회.
* **정보 수정**: 연락처 및 비밀번호 변경 기능 (BCrypt 암호화 적용).

### 시스템 공통

* **전자결재 시스템**: 모든 모듈의 주요 액션(등록/수정/삭제) 시 결재 문서 생성 → 상급자 승인 후 로직 실행.
* **실시간 알림**: 결재 대기 문서 발생 시 SSE를 통해 즉시 알림 전송.

## 4. 시스템 아키텍처 및 DB 구조

### 4.1 소프트웨어 아키텍처 (Layered Architecture)

Spring Boot의 표준 계층형 아키텍처를 준수하여 유지보수성과 확장성을 확보했습니다.

* **Controller Layer**: 클라이언트 요청 처리 및 응답 반환 (RESTful API & View).
* **Service Layer**: 비즈니스 로직 수행, 트랜잭션 관리 (`@Transactional`), 결재 로직 처리.
* **Mapper Layer (MyBatis)**: SQL 매핑 및 DB 액세스.
* **Interceptor**: `TenantContextInterceptor`를 통해 요청마다 세션의 `company_id`를 확인하고 DB 컨텍스트를 스위칭.

### 4.2 멀티 테넌시 (Multi-tenancy) 전략

하나의 애플리케이션 인스턴스로 여러 회사의 데이터를 격리하여 서비스합니다.

* **Database per Tenant**: 로그인 시 사용자의 `company_id`를 식별하여 `TenantRoutingDataSource`가 해당 회사의 독립적인 DB(`db_{사업자번호}`)로 커넥션을 동적으로 전환합니다.
* **관리 DB (Tenant Registry)**: 사용자 계정, 회사 정보, 테넌트 메타데이터는 별도의 공통 관리 DB에서 통합 관리됩니다.

### 4.3 배포 아키텍처

* **AWS EC2**: Ubuntu 환경 호스팅.
* **Nginx**: 80 포트 요청을 8080(Spring Boot)으로 포워딩하는 리버스 프록시(Reverse Proxy) 역할 수행.
* **Docker**: MySQL DB를 컨테이너로 구동하여 관리 및 배포 용이성 확보.
* **Background Execution**: `nohup`을 사용하여 백그라운드에서 애플리케이션 무중단 실행.

## 5. 설치 및 실행 방법

### 로컬 개발 환경 설정

#### 1. Repository Clone

```bash
git clone [https://github.com/wsyoo9999/Spring05_Team2_y2k4_FINAL-PJT.git](https://github.com/wsyoo9999/Spring05_Team2_y2k4_FINAL-PJT.git)
````

#### 2. Database 설정 (MySQL)

* 로컬 MySQL 또는 Docker 컨테이너를 실행합니다.
* `src/main/resources/application.yml` 파일의 DB 접속 정보를 본인의 환경에 맞게 수정합니다.

```yaml
app:
  mysql:
    host: localhost
    port: 3306
    username: {YOUR_DB_USERNAME}
    password: {YOUR_DB_PASSWORD}
```

#### 3. 외부 API 설정 (사업자 번호 조회)

* 본 프로젝트는 회사 정보 조회를 위해 **Bizno API**를 사용합니다.
* 정상적인 기능 동작을 위해서는 API 키 발급 및 설정이 필요합니다.

1. [Bizno.net](https://bizno.net/)에 접속하여 회원가입 및 API Key를 발급받습니다.
2. 프로젝트 내 API 호출 로직(또는 설정 파일)에 발급받은 Key를 입력합니다.

#### 4. 빌드 및 실행

```bash
# Gradle 빌드 및 실행
./gradlew bootRun
```

## 6. 팀원 및 역할 (Roles)

* **유원상 (팀장)**: [`@wsyoo9999`](https://github.com/wsyoo9999)

  * DB 설계(멀티 테넌시), 판매/구매(Transaction) 파트, SSE 알림, AWS 배포(EC2/Nginx).

* **김민수**: [`@Mawoolight`](https://github.com/Mawoolight)

  * 생산/제조(Production) 파트, 결재 시스템 로직 고도화, UI/UX 디자인 및 개선.

* **김세현**: [`@TheSnailBride`](https://github.com/TheSnailBride)

  * 인사(HR) 파트, 근태/급여 관리, 권한 관리(Spring Security), 계정 보안 시스템.

* **김재이**: [`@Jay-3077`](https://github.com/Jay-3077)

  * 재고(Stock) 파트, 입출고 로직, 대시보드 탭 디자인, 로그인/회원가입.

---

**Copyright © 2025 Team Y2K4. All Rights Reserved.**
