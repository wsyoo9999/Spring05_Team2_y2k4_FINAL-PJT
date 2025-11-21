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
            @RequestParam(required = false) String phone_number,
            @RequestParam(required = false) Integer supervisor
    ) {
        try {
            Employee newEmployee = new Employee();
            newEmployee.setEmp_name(emp_name);
            newEmployee.setPosition(position);
            newEmployee.setHire_date(hire_date);
            newEmployee.setStatus(status);
            newEmployee.setDept_name(dept_name);
            newEmployee.setPhone_number(phone_number);
            newEmployee.setSupervisor(supervisor);

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

    @PostMapping("/requestVacation")
    public boolean requestVacation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason,
            HttpSession session) {

        try {

            Integer empIdObj = (Integer) session.getAttribute("emp_id");
            String empName = (String) session.getAttribute("emp_name");

            if (empIdObj == null) return false; // 로그인 정보 오류

            Employee me = employeeService.getEmployeeDetail(empIdObj);
            Long supervisorId = null;
            if (me != null && me.getSupervisor() != null) {
                supervisorId = Long.valueOf(me.getSupervisor());
            }

            Long requesterEmpId = Long.valueOf(empIdObj);

            // 2. JSON 데이터 생성 (결재 문서 본문용)
            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 4);
            payload.put("tb_id", 0);
            payload.put("cd_id", 0);
            payload.put("requesterName", empName);
            payload.put("startDate", startDate.toString());
            payload.put("endDate", endDate.toString());
            payload.put("reason", reason);

            // 3. 문서 객체 생성
            Documents doc = new Documents();
            doc.setTitle("[휴가신청] " + empName + " (" + startDate + " ~ " + endDate + ")");
            doc.setReq_id(requesterEmpId);
            doc.setReq_date(LocalDate.now());
            doc.setQuery(objectMapper.writeValueAsString(payload));

            doc.setCat_id(4); // 카테고리: 인사
            doc.setTb_id(0);  // 세부: 휴가 요청
            doc.setCd_id(0);  // 분류: 추가(신청)

            doc.setStatus(0);     // 0: 대기 (승인 전)
            doc.setAppr_id(supervisorId); // 결재자

            documentsService.addDocument(doc);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * [추가] 퇴직 처리 결재 요청
     */
    @PostMapping("/employees/request-status-change")
    public boolean requestResignation(@RequestBody Map<String, Object> reqData, HttpSession session) {
        try {
            // 1. 세션 정보 (기안자)
            Integer requesterId = (Integer) session.getAttribute("emp_id");
            String requesterName = (String) session.getAttribute("emp_name");
            if (requesterId == null) return false;

            Employee requester = employeeService.getEmployeeDetail(requesterId);
            Long supervisorId = null;
            if (requester != null && requester.getSupervisor() != null) {
                supervisorId = Long.valueOf(requester.getSupervisor());
            }

            // 2. 클라이언트 데이터 (대상 직원)
            Integer targetEmpId = Integer.parseInt(reqData.get("emp_id").toString());
            String targetEmpName = (String) reqData.get("emp_name");
            String reason = (String) reqData.get("reason");
            String newStatus = (String) reqData.get("newStatus"); // 프론트에서 보낸 변경 상태

            // 3. 결재 문서 Payload 생성
            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 4);   // 인사
            payload.put("tb_id", 1);
            payload.put("cd_id", 1);
            payload.put("targetEmpId", targetEmpId);
            payload.put("targetEmpName", targetEmpName);
            payload.put("newStatus", newStatus);
            payload.put("reason", reason);

            // 4. 문서 생성
            Documents doc = new Documents();
            doc.setTitle("[인사] " + targetEmpName + " " + newStatus + " 처리 요청");
            doc.setReq_id(Long.valueOf(requesterId));
            doc.setReq_date(LocalDate.now());
            doc.setQuery(objectMapper.writeValueAsString(payload));


            doc.setCat_id(4);
            doc.setTb_id(1); // 퇴사 요청은 1번으로 설정
            doc.setCd_id(1);

            doc.setStatus(0);      // 대기
            doc.setAppr_id(supervisorId);
            // 5. 저장
            documentsService.addDocument(doc);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}