package com.multi.y2k4.mapper.tenant.alert;

import com.multi.y2k4.vo.alert.Alert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlertMapper {
    public List<Alert> selectAlerts(Long emp_id);
    public int updateIsRead(@Param("alert_id") int alert_id);
    public int deleteAlert(@Param("emp_id") Integer emp_id);
    public int insertAlert(Alert alert);

    void insertAlertForApprover(@Param("doc_id") Long docId, @Param("alert_type") Integer alertType);
}