package com.multi.y2k4.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.hr.AttendanceService;
import com.multi.y2k4.service.hr.EmployeeService;
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.hr.Attendance;
import com.multi.y2k4.vo.hr.Employee;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor // ✨ 4. @RequiredArgsConstructor 추가 (생성자 주입용)
public class HRController {

    // 5. EmployeeService만 주입
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final DocumentsService documentsService;
    private final ObjectMapper objectMapper;

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

    /**
     * [추가] 신규 직원 등록 (addEmployee.html 팝업에서 호출)
     */
    @PostMapping("/employees/add")
    public boolean addEmployee(
            @RequestParam String emp_name,
            @RequestParam String position,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hire_date,
            @RequestParam String status,
            @RequestParam(required = false) String dept_name,
            @RequestParam(required = false) String phone_number
    ) {
        try {
            Employee newEmployee = new Employee();
            newEmployee.setEmp_name(emp_name);
            newEmployee.setPosition(position);
            newEmployee.setHire_date(hire_date);
            newEmployee.setStatus(status);
            newEmployee.setDept_name(dept_name);
            newEmployee.setPhone_number(phone_number);

            return employeeService.addEmployee(newEmployee) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
// ================================================================
    // 2. 근태 관리 ( [수정] Service와 연결 )
    // ================================================================

    /**
     * [추가] 일일 근태 기록 일괄 생성 API
     */
    @PostMapping("/attendance/generate")
    public boolean generateDailyAttendance() {
        try {
            return attendanceService.generateDailyAttendance();
        } catch (Exception e) {
            // (예: 중복 키 오류 등)
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/attendance")
    public List<Attendance> getAttendanceList(
            @RequestParam(required = false) String search_keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date) {

        // [수정] 실제 서비스 호출
        return attendanceService.getAttendanceList(search_keyword, start_date, end_date);
    }

    @GetMapping("/attendance/{attendanceId}")
    public Attendance getAttendanceDetail(@PathVariable Integer attendanceId) {
        // [수정] 실제 서비스 호출
        return attendanceService.getAttendanceDetail(attendanceId);
    }

    @PutMapping("/attendance/{attendanceId}")
    public boolean updateAttendanceStatus(@PathVariable Integer attendanceId, @RequestBody Attendance updatedAtt) {
        // [수정] 실제 서비스 호출
        updatedAtt.setAttendance_id(attendanceId); // URL의 ID를 VO에 설정
        return attendanceService.updateAttendanceStatus(updatedAtt);
    }

    // ================================================================
    // 3. 급여 관리  임시로 빈 값 반환 - 추후 연동)
    // ================================================================

//
//    /**
//     * 급여 대장 조회
//     */
//    @GetMapping("/salary")
//    public List<Salary> getSalaryList(
//            @RequestParam(required = false) Integer search_year,
//            @RequestParam(required = false) Integer search_month) {
//
//        // ✨ 7. 서비스 없이 빈 목록 반환
//        return Collections.emptyList();
//    }
//
//    /**
//     * 급여 상세 조회
//     */
//    @GetMapping("/salary/{salaryId}")
//    public Salary getSalaryDetail(@PathVariable Integer salaryId) {
//        // ✨ 7. 서비스 없이 null 반환
//        return null;
//    }

    /**
     * [신규] 휴가 신청 API
     * requestVacation.html 팝업에서 호출됨
     */
    @PostMapping("/requestVacation")
    public boolean requestVacation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason,
            HttpSession session) {

        try {
            // 1. 기안자 정보 설정 (로그인한 사용자)
            // TODO: 실제로는 세션에 저장된 로그인 ID를 통해 DB에서 emp_id와 emp_name을 조회해야 합니다.
            // 현재는 테스트를 위해 임의의 값(1번 사원)을 사용합니다.
            Long requesterEmpId = 1L;
            String requesterName = (String) session.getAttribute("id");
            if (requesterName == null) requesterName = "기안자";

            // 2. 결재 문서 본문에 들어갈 데이터(JSON) 생성
            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 4);   // 카테고리: 4 (인사)
            payload.put("tb_id", 0);    // 테이블: 0 (휴가/퇴직)
            payload.put("cd_id", 0);    // 동작: 0 (추가/신청)

            // 휴가 신청서 고유 데이터 담기
            payload.put("requesterName", requesterName);
            payload.put("startDate", startDate.toString());
            payload.put("endDate", endDate.toString());
            payload.put("reason", reason);

            // 3. 결재 문서(Documents) 객체 생성 및 설정
            Documents doc = new Documents();
            doc.setTitle("[휴가신청] " + requesterName + " (" + startDate + " ~ " + endDate + ")");
            doc.setReq_id(requesterEmpId);
            doc.setReq_date(LocalDate.now());
            doc.setStatus(0); // 0: 결재 대기 상태

            // 4. 결재자 지정
            // TODO: 실제로는 기안자의 부서장이나 상급자(supervisor)를 조회하여 설정해야 합니다.
            doc.setAppr_id(1L); // 테스트용: 1번 사원에게 결재 요청

            // 5. payload 맵을 JSON 문자열로 변환하여 query 필드에 저장
            String query = objectMapper.writeValueAsString(payload);
            doc.setQuery(query);

            // 6. DB에 결재 문서 저장 (이때 DocumentBodyBuilder가 호출되어 본문 HTML 생성)
            documentsService.addDocument(doc);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

