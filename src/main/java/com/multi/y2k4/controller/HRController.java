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

        Integer myId = (Integer) session.getAttribute("emp_id");
        if (myId == null) return false;

        Employee me = employeeService.getEmployeeDetail(myId);
        Employee target = employeeService.getEmployeeDetail(empId);

        if (me == null || target == null) return false;

        updatedEmp.setEmp_id(empId);


        // A. 부서 이동
        if (updatedEmp.getDept_name() != null && !updatedEmp.getDept_name().equals(target.getDept_name())) {
            if (POSITION_EMP.equals(me.getPosition())) return false;
            if (POSITION_MID.equals(me.getPosition())) {
                boolean isMyDept = Objects.equals(me.getDept_name(), target.getDept_name());
                boolean isTargetEmp = POSITION_EMP.equals(target.getPosition());
                if (!isMyDept || !isTargetEmp) return false;
            }
        }

        // B. 직급 변경
        // -> 요청자가 '최상위 관리자'가 아니면 무조건 차단
        if (updatedEmp.getPosition() != null && !updatedEmp.getPosition().equals(target.getPosition())) {
            if (!POSITION_TOP.equals(me.getPosition())) {
                System.out.println("권한 없음: 직급 변경은 최상위 관리자만 가능합니다.");
                return false;
            }
        }

        // C. 직속상관 변경
        if (updatedEmp.getSupervisor() != null && !Objects.equals(updatedEmp.getSupervisor(), target.getSupervisor())) {
            if (!POSITION_TOP.equals(me.getPosition())) return false;
        }

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
            @RequestParam(required = false) Integer supervisor,
            HttpSession session
    ) {
        try {

            Integer myId = (Integer) session.getAttribute("emp_id");
            Employee me = employeeService.getEmployeeDetail(myId);

            // (1) 사원은 아예 등록 불가
            if (POSITION_EMP.equals(me.getPosition())) {
                System.out.println("권한 없음: 사원은 직원을 등록할 수 없습니다.");
                return false;
            }

            // (2) 중간 관리자는 '최상위 관리자'를 생성할 수 없음
            if (POSITION_MID.equals(me.getPosition())) {
                if (POSITION_TOP.equals(position)) {
                    System.out.println("권한 없음: 중간 관리자는 최상위 관리자를 생성할 수 없습니다.");
                    return false;
                }
            }

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

    //최상위 관리자 로직
    private Long determineApprover(Employee me) {
        // 1. 본인이 최상위 관리자라면 스스로 승인
        if (POSITION_TOP.equals(me.getPosition())) {
            return Long.valueOf(me.getEmp_id());
        }

        // 2. 직속 상사가 지정되어 있다면 -> 직속 상사에게 결재 요청
        if (me.getSupervisor() != null) {
            return Long.valueOf(me.getSupervisor());
        }

        // 3. 직속 상사가 없다면 -> '최상위 관리자'를 찾아서 결재 요청
        List<Employee> topManagers = employeeService.getEmployeeList(null, null, POSITION_TOP, null);
        if (topManagers != null && !topManagers.isEmpty()) {
            // 최상위 관리자가 여러 명일 경우 첫 번째 사람 지정
            return Long.valueOf(topManagers.get(0).getEmp_id());
        }

        // 4. 상사도 없고 최상위 관리자도 없는 경우 (예외 상황)
        return null;
    }

    private int getRank(String position) {
        if (position == null) return 0;

        // 공백 유무 등 유연하게 처리하기 위해 contains 또는 trim 사용 권장
        String pos = position.trim();

        if (POSITION_TOP.equals(pos) || "최고관리자".equals(pos) || "최상위 관리자".equals(pos)) {
            return 3;
        }
        if (POSITION_MID.equals(pos) || "중간관리자".equals(pos) || "중간 관리자".equals(pos)) {
            return 2;
        }
        return 1; // 사원 및 기타
    }


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

            Long approverId = determineApprover(me);

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


            // 3. 데이터 파싱 및 문서 생성
            Integer targetEmpId = Integer.parseInt(reqData.get("emp_id").toString());
            Employee target = employeeService.getEmployeeDetail(targetEmpId);


            if (getRank(me.getPosition()) < getRank(target.getPosition())) {
                System.out.println("권한 없음: 하위 직급자가 상위 직급자의 인사 발령을 신청할 수 없습니다.");
                return false;
            }

            Long approverId = determineApprover(me);

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