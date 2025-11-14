package com.multi.y2k4.mapper.tenant.hr;

import com.multi.y2k4.vo.hr.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 직원 목록 조회 (검색 및 정렬 기능 포함)
     * @param search_name 이름 검색어
     * @param search_dept 부서 검색어
     * @param search_position 직급 검색어
     * @param sort 정렬 조건 (예: "emp_id,asc")
     * @return 직원 목록
     */
    List<Employee> getEmployeeList(
            @Param("search_name") String search_name,
            @Param("search_dept") String search_dept,
            @Param("search_position") String search_position,
            @Param("sort") String sort
    );

    /**
     * 직원 상세 조회
     */
    Employee getEmployeeDetail(@Param("empId") Integer empId);

    /**
     * 직원 정보 수정
     */
    int updateEmployee(Employee employee);

    /**
     * [추가] 신규 직원 등록
     */
    int addEmployee(Employee employee);
}