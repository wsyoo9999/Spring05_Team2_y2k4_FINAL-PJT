package com.multi.y2k4.service.alert;

import com.multi.y2k4.mapper.tenant.alert.AlertMapper;
import com.multi.y2k4.vo.alert.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertMapper alertMapper;

    public List<Alert> selectAlerts(Long emp_id) {
        return alertMapper.selectAlerts(emp_id);
    }


    public int creatAlert(Long emp_id, Long doc_id) {
        Alert alert = new Alert();
        alert.setEmp_id(emp_id);
        alert.setDoc_id(doc_id);
        alert.setIs_read(0);

        return alertMapper.insertAlert(alert);
    }

    public void createAlertForApprover(Long doc_id, Integer alertType) {
        alertMapper.insertAlertForApprover(doc_id, alertType);
    }
}