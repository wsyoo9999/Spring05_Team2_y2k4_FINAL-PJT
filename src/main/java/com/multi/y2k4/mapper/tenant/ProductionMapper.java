package com.multi.y2k4.mapper.tenant;

import com.multi.y2k4.vo.production.BOM;
import com.multi.y2k4.vo.production.Defect;
import com.multi.y2k4.vo.production.Lot;
import com.multi.y2k4.vo.production.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 생산/제조 모듈용 매퍼 (work_order, bom, lot, defect 테이블)
 이 매퍼는 Tenant DB에 연결.
 */
@Mapper
public interface ProductionMapper {

    /**
     작업지시서 목록 조회 (검색 기능 포함)
     @param order_status 상태
     @param stock_id     제품코드
     @param start_date   시작일
     @param due_date     완료일
     @return 작업지시서 목록
     */
    List<WorkOrder> getWorkOrderList(@Param("order_status") String order_status,
                                     @Param("stock_id") Long stock_id,
                                     @Param("start_date") String start_date,
                                     @Param("due_date") String due_date);

    /**
     작업지시서 상세 조회
     @param work_order_id
     @return 작업지시서 상세 정보
     */
    WorkOrder getWorkOrderDetail(@Param("work_order_id") Long work_order_id);

    /**
     작업지시서별 Lot 목록 조회
     @param work_order_id
     @return Lot 목록
     */
    List<Lot> getWorkOrderLots(@Param("work_order_id") Long work_order_id);

    /**
     작업지시서별 불량 내역 조회
     @param work_order_id
     @return 불량 내역 목록
     */
    List<Defect> getWorkOrderDefects(@Param("work_order_id") Long work_order_id);

    /**
     BOM 목록 조회 (검색 포함)
     @param parent_stock_id
     @return BOM 목록
     */
    List<BOM> getBOMList(@Param("parent_stock_id") Long parent_stock_id);

    /**
     작업지시서 신규 등록
     @param workOrder
     @return 삽입된 행 수 (보통 1)
     */
    int addWorkOrder(WorkOrder workOrder);

    // (필요에 따라 생산 실적 등록, 불량 등록 등 추가)

}