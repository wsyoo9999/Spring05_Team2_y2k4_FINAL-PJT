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
                                     @Param("stock_name") String stock_name,
                                     @Param("start_date") String start_date,
                                     @Param("due_date") String due_date);

    WorkOrder getWorkOrderDetail(@Param("work_order_id") Long work_order_id);

    List<Lot> getWorkOrderLots(@Param("work_order_id") Long work_order_id);

    List<Defect> getWorkOrderDefects(@Param("work_order_id") Long work_order_id);

    List<BOM> getBOMList(@Param("parent_stock_name") String parent_stock_name,
                         @Param("child_stock_name") String child_stock_name);
    int addWorkOrder(WorkOrder workOrder);

    int addBOM(BOM bom);
    int deleteWorkOrder(@Param("work_order_id") Long work_order_id);
    BOM getBOMById(@Param("bom_id") Long bom_id);
    int updateBOM(BOM bom);
    int deleteBOM(@Param("bom_id") Long bom_id);
    int addLot(Lot lot);
    Lot getLotById(@Param("lot_id") Long lot_id);
    void updateWorkOrderProgress(
            @Param("work_order_id") Long work_order_id,
            @Param("good_qty") Integer good_qty,
            @Param("defect_qty") Integer defect_qty, // 추가됨
            @Param("order_status") int order_status
    );
    int addDefect(Defect defect);
    int deleteDefectsByWorkOrderId(@Param("work_order_id") Long work_order_id);
    int deleteLotsByWorkOrderId(@Param("work_order_id") Long work_order_id);
}