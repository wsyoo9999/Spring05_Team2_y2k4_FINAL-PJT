package com.multi.y2k4.mapper.tenant.production;

import com.multi.y2k4.vo.production.BOM;
import com.multi.y2k4.vo.production.Defect;
import com.multi.y2k4.vo.production.Lot;
import com.multi.y2k4.vo.production.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductionMapper {

    List<WorkOrder> getWorkOrderList(@Param("order_status") String order_status,
                                     @Param("stock_id") Long stock_id,
                                     @Param("start_date") String start_date,
                                     @Param("due_date") String due_date);

    WorkOrder getWorkOrderDetail(@Param("work_order_id") Long work_order_id);

    List<Lot> getWorkOrderLots(@Param("work_order_id") Long work_order_id);

    List<Defect> getWorkOrderDefects(@Param("work_order_id") Long work_order_id);

    List<BOM> getBOMList(@Param("parent_stock_id") Long parent_stock_id,
                         @Param("child_stock_id") Long child_stock_id); // 2. 파라미터 추가
    int addWorkOrder(WorkOrder workOrder);

    int addBOM(BOM bom);
    int deleteWorkOrder(@Param("work_order_id") Long work_order_id);
    BOM getBOMById(@Param("bom_id") Long bom_id);
    int updateBOM(BOM bom);
    int deleteBOM(@Param("bom_id") Long bom_id);
}