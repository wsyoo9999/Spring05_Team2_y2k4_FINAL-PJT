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
import java.util.Objects;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor // ✨ 4. @RequiredArgsConstructor 추가 (생성자 주입용)
public class HRController {

    // 5. EmployeeService만 주입
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final DocumentsService documentsService;
    private final ObjectMapper objectMapper;

    private static final String POSITION_EMP = "사원";
    private static final String POSITION_MID = "중간 관리자";
    private static final String POSITION_TOP = "최상위 관리자";

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
    public boolean updateEmployee(@PathVariable Integer empId,
                                  @RequestBody Employee updatedEmp,
                                  HttpSession session) {

        // 1. 내 정보(요청자) 확인
        Integer myId = (Integer) session.getAttribute("emp_id");
        if (myId == null) return false;
        Employee me = employeeService.getEmployeeDetail(myId);

        // 2. 대상 정보(타겟) 확인
        Employee target = employeeService.getEmployeeDetail(empId);
        if (target == null) return false;

        updatedEmp.setEmp_id(empId); // ID 고정

        // ================== 권한 검증 로직 시작 ==================

        // A. 부서 이동 (dept_name 변경 시)
        if (updatedEmp.getDept_name() != null && !updatedEmp.getDept_name().equals(target.getDept_name())) {
            if (POSITION_EMP.equals(me.getPosition())) {
                System.out.println("권한 없음: 사원은 부서 이동 불가");
                return false;
            }
            if (POSITION_MID.equals(me.getPosition())) {
                // 중간관리자는 "자기 부서"의 "사원"만 이동 가능
                boolean isMyDept = Objects.equals(me.getDept_name(), target.getDept_name());
                boolean isTargetEmp = POSITION_EMP.equals(target.getPosition());
                if (!isMyDept || !isTargetEmp) {
                    System.out.println("권한 없음: 중간관리자는 자기 부서 사원만 이동 가능");
                    return false;
                }
            }
            // 최고관리자는 제약 없음
        }

        // B. 직급 변경 (position 변경 시)
        if (updatedEmp.getPosition() != null && !updatedEmp.getPosition().equals(target.getPosition())) {
            if (POSITION_EMP.equals(me.getPosition())) {
                System.out.println("권한 없음: 사원은 직급 변경 불가");
                return false;
            }
            if (POSITION_MID.equals(me.getPosition())) {
                // 중간관리자는 "자기 부서"의 "사원"을 -> "중간관리자"로만 변경 가능
                boolean isMyDept = Objects.equals(me.getDept_name(), target.getDept_name());
                boolean isTargetEmp = POSITION_EMP.equals(target.getPosition());
                boolean isPromotingToMid = POSITION_MID.equals(updatedEmp.getPosition());

                if (!isMyDept || !isTargetEmp || !isPromotingToMid) {
                    System.out.println("권한 없음: 중간관리자 권한 범위 초과");
                    return false;
                }
            }
            // 최고관리자는 제약 없음 (사원 <-> 중간관리자 등 자유)
        }

        // C. 직속상관 변경 (supervisor 변경 시)
        if (updatedEmp.getSupervisor() != null && !Objects.equals(updatedEmp.getSupervisor(), target.getSupervisor())) {
            // 오직 최고관리자만 변경 가능
            if (!POSITION_TOP.equals(me.getPosition())) {
                System.out.println("권한 없음: 직속상관 변경은 최고관리자만 가능");
                return false;
            }
        }

        // ================== 권한 검증 로직 종료 ==================

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
            // 1. 세션에서 내 정보 가져오기
            Integer empIdObj = (Integer) session.getAttribute("emp_id");
            String empName = (String) session.getAttribute("emp_name");

            if (empIdObj == null) return false;
            Employee me = employeeService.getEmployeeDetail(empIdObj);

            Long requesterEmpId = Long.valueOf(empIdObj);
            Long approverId = null;

            // 2. 직급별 결재자(appr_id) 지정 로직
            if (POSITION_EMP.equals(me.getPosition())) {
                // 사원 -> 직속상관 (없으면 최고관리자 로직 등 추가 가능, 일단 직속상관)
                if (me.getSupervisor() != null) {
                    approverId = Long.valueOf(me.getSupervisor());
                }
            } else if (POSITION_MID.equals(me.getPosition())) {
                // 중간관리자 -> 최고관리자 (중간관리자의 직속상관은 보통 최고관리자)
                if (me.getSupervisor() != null) {
                    approverId = Long.valueOf(me.getSupervisor());
                }
            } else if (POSITION_TOP.equals(me.getPosition())) {
                // 최고관리자 -> 스스로 승인
                approverId = requesterEmpId;
            }

            // 2. JSON 데이터 생성
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
            doc.setStatus(0); // 대기
            doc.setAppr_id(approverId); // 계산된 결재자 ID

            doc.setStatus(0); // 대기


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
    public boolean requestStatusChange(@RequestBody Map<String, Object> reqData, HttpSession session) {
        try {
            // 1. 기안자 정보
            Integer requesterId = (Integer) session.getAttribute("emp_id");
            if (requesterId == null) return false;
            Employee me = employeeService.getEmployeeDetail(requesterId);

            Long approverId = null;

            // 2. 직급별 결재자(appr_id) 지정 로직 (휴가와 동일 원칙)
            if (POSITION_EMP.equals(me.getPosition())) {
                // 사원 -> 직속상관 (또는 최고관리자)
                if (me.getSupervisor() != null) approverId = Long.valueOf(me.getSupervisor());
            } else if (POSITION_MID.equals(me.getPosition())) {
                // 중간관리자 -> 최고관리자
                if (me.getSupervisor() != null) approverId = Long.valueOf(me.getSupervisor());
            } else if (POSITION_TOP.equals(me.getPosition())) {
                // 최고관리자 -> 스스로 승인
                approverId = Long.valueOf(requesterId);
            }

            // 3. 데이터 파싱 및 문서 생성
            Integer targetEmpId = Integer.parseInt(reqData.get("emp_id").toString());
            String targetEmpName = (String) reqData.get("emp_name");
            String currentStatus = (String) reqData.get("currentStatus");
            String newStatus = (String) reqData.get("newStatus");
            String reason = (String) reqData.get("reason");

            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 4);
            payload.put("tb_id", 1); // 퇴직/상태변경
            payload.put("cd_id", 1);
            payload.put("targetEmpId", targetEmpId);
            payload.put("targetEmpName", targetEmpName);
            payload.put("currentStatus", currentStatus);
            payload.put("newStatus", newStatus);
            payload.put("reason", reason);

            Documents doc = new Documents();
            doc.setTitle("[인사발령] " + targetEmpName + " " + newStatus + " 신청");
            doc.setReq_id(Long.valueOf(requesterId));
            doc.setReq_date(LocalDate.now());
            doc.setQuery(objectMapper.writeValueAsString(payload));
            doc.setStatus(0);
            doc.setAppr_id(approverId); // 계산된 결재자

            documentsService.addDocument(doc);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}