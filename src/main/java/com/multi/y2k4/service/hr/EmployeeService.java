package com.multi.y2k4.service.hr;

import com.multi.y2k4.mapper.tenant.hr.EmployeeMapper;
import com.multi.y2k4.vo.hr.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;

    /**
     * 직원 목록 조회 (검색/정렬 조건 포함)
     */
    public List<Employee> getEmployeeList(String search_name, String search_dept, String search_position, String sort) {
        return employeeMapper.getEmployeeList(search_name, search_dept, search_position, sort);
    }

    public int addEmployee(Employee employee) {
        return employeeMapper
                .addEmployee(employee);
    }

    /**
     * 직원 상세 조회
     */
    public Employee getEmployeeDetail(Integer empId) {
        return employeeMapper.getEmployeeDetail(empId);
    }

    /**
     * 직원 정보 수정
     * @return 수정 성공 시 true
     */
    public boolean updateEmployee(Employee employee) {
        // updateEmployee는 0 또는 1 (영향받은 행 수)을 반환하므로 0보다 큰지 확인
        return employeeMapper.updateEmployee(employee) > 0;
    }
}