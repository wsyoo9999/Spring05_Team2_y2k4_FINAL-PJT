package com.multi.y2k4.controller;

import com.multi.y2k4.vo.hr.Employee;
import com.multi.y2k4.vo.hr.Attendance;
import com.multi.y2k4.vo.hr.Salary;
import com.multi.y2k4.service.hr.EmployeeService; // ✨ 1. EmployeeService만 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections; // ✨ 2. Collections 임포트 (빈 목록 반환용)
import java.util.List;
// 3. (더미 데이터 관련 임포트 모두 제거)

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor // ✨ 4. @RequiredArgsConstructor 추가 (생성자 주입용)
public class HRController {

    // 5. EmployeeService만 주입
    private final EmployeeService employeeService;


    // ================================================================
    // 1. 직원 관리  실제 DB 연동)
    // ================================================================

    /**
     * 직원 목록 조회
     */
    @GetMapping("/employees")
    public List<Employee> getEmployeeList(
            @RequestParam(required = false) String search_name,
            @RequestParam(required = false) String search_dept,
            @RequestParam(required = false) String search_position,
            @RequestParam(required = false, defaultValue = "emp_id,asc") String sort) {

        // EmployeeService (실제 DB) 호출
        return employeeService.getEmployeeList(search_name, search_dept, search_position, sort);
    }

    /**
     * 직원 상세 조회
     */
    @GetMapping("/employees/{empId}")
    public Employee getEmployeeDetail(@PathVariable Integer empId) {
        // EmployeeService (실제 DB) 호출
        return employeeService.getEmployeeDetail(empId);
    }

    /**
     * 직원 정보 수정
     */
    @PutMapping("/employees/{empId}")
    public boolean updateEmployee(@PathVariable Integer empId, @RequestBody Employee updatedEmp) {
        updatedEmp.setEmp_id(empId); // URL의 ID를 VO에 설정
        // EmployeeService (실제 DB) 호출
        return employeeService.updateEmployee(updatedEmp);
    }

    // ================================================================
    // 2. 근태 관리 ( 임시로 빈 값 반환 - 추후 연동)
    // ================================================================

    /**
     * 근태 현황 조회
     */
    @GetMapping("/attendance")
    public List<Attendance> getAttendanceList(
            @RequestParam(required = false) String search_keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date) {

        // ✨ 7. 서비스 없이 빈 목록 반환
        return Collections.emptyList();
    }

    /**
     * 근태 기록 상세 조회
     */
    @GetMapping("/attendance/{attendanceId}")
    public Attendance getAttendanceDetail(@PathVariable Integer attendanceId) {
        // ✨ 7. 서비스 없이 null 반환
        return null;
    }

    /**
     * 근태 기록 상태 수정
     */
    @PutMapping("/attendance/{attendanceId}")
    public boolean updateAttendanceStatus(@PathVariable Integer attendanceId, @RequestBody Attendance updatedAtt) {
        // 7. 서비스 없이 false 반환
        return false;
    }

    // ================================================================
    // 3. 급여 관리  임시로 빈 값 반환 - 추후 연동)
    // ================================================================

    /**
     * 급여 대장 조회
     */
    @GetMapping("/salary")
    public List<Salary> getSalaryList(
            @RequestParam(required = false) Integer search_year,
            @RequestParam(required = false) Integer search_month) {

        // ✨ 7. 서비스 없이 빈 목록 반환
        return Collections.emptyList();
    }

    /**
     * 급여 상세 조회
     */
    @GetMapping("/salary/{salaryId}")
    public Salary getSalaryDetail(@PathVariable Integer salaryId) {
        // ✨ 7. 서비스 없이 null 반환
        return null;
    }
}

