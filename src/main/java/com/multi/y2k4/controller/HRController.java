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
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class HRController {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final DocumentsService documentsService;
    private final ObjectMapper objectMapper;

    // 직급 상수 정의
    private static final String POS_EMP = "사원";
    private static final String POS_MID = "중간 관리자";
    private static final String POS_TOP = "최상위 관리자";

    // ================================================================
    // 1. 직원 관리
    // ================================================================

    @GetMapping("/employees")
    public List<Employee> getEmployeeList(
            @RequestParam(required = false) String search_name,
            @RequestParam(required = false) String search_dept,
            @RequestParam(required = false) String search_position,
            @RequestParam(required = false, defaultValue = "emp_id,asc") String sort) {
        return employeeService.getEmployeeList(search_name, search_dept, search_position, sort);
    }

    @GetMapping("/employees/{empId}")
    public Employee getEmployeeDetail(@PathVariable Integer empId) {
        return employeeService.getEmployeeDetail(empId);
    }

    /**
     * 직원 정보 수정 (권한 매트릭스 적용)
     */
    @PutMapping("/employees/{empId}")
    public ResponseEntity<String> updateEmployee(@PathVariable Integer empId,
                                                 @RequestBody Employee updatedEmp,
                                                 HttpSession session) {

        Integer myId = (Integer) session.getAttribute("emp_id");
        if (myId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

        Employee me = employeeService.getEmployeeDetail(myId);
        Employee target = employeeService.getEmployeeDetail(empId); // 수정 대상(Before)

        if (me == null || target == null) {
            return ResponseEntity.badRequest().body("사용자 또는 대상 직원 정보를 찾을 수 없습니다.");
        }

        // 최상위 관리자는 모든 권한 허용
        if (POS_TOP.equals(me.getPosition())) {
            updatedEmp.setEmp_id(empId);
            boolean success = employeeService.updateEmployee(updatedEmp);
            return success ? ResponseEntity.ok("수정 성공") : ResponseEntity.internalServerError().body("수정 실패");
        }

        // 사원은 수정 권한 없음
        if (POS_EMP.equals(me.getPosition())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 사원은 수정 권한이 없습니다.");
        }

        // === 중간 관리자 권한 검증 로직 ===
        if (POS_MID.equals(me.getPosition())) {
            // 1) 타 부서 직원은 수정 불가
            if (!Objects.equals(me.getDept_name(), target.getDept_name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 중간 관리자는 타 부서 직원을 수정할 수 없습니다.");
            }

            // 2) 상위 직급(최상위)이나 같은 중간 관리자 수정 불가 (하위 직급만 가능)
            if (getRank(target.getPosition()) >= getRank(me.getPosition())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 중간 관리자는 동급 또는 상위 직급을 수정할 수 없습니다.");
            }

            // 3) 직급 변경 (승진) 검증
            if (updatedEmp.getPosition() != null && !updatedEmp.getPosition().equals(target.getPosition())) {
                // '사원' -> '중간 관리자'로만 변경 가능
                if (!POS_EMP.equals(target.getPosition()) || !POS_MID.equals(updatedEmp.getPosition())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 중간 관리자는 소속 사원을 중간 관리자로만 승진시킬 수 있습니다.");
                }
            }

            // 4) 직속 상관 변경 검증 (불가)
            if (updatedEmp.getSupervisor() != null && !Objects.equals(updatedEmp.getSupervisor(), target.getSupervisor())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 중간 관리자는 직속 상관을 변경할 수 없습니다.");
            }
        }

        updatedEmp.setEmp_id(empId);
        boolean success = employeeService.updateEmployee(updatedEmp);

        if (success) {
            return ResponseEntity.ok("직원 정보가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.internalServerError().body("서버 오류: 정보 수정에 실패했습니다.");
        }
    }

    /**
     * 신규 직원 등록 (권한 매트릭스 적용)
     */
    @PostMapping("/employees/add")
    public ResponseEntity<String> addEmployee(
            @RequestParam String emp_name,
            @RequestParam String position, // 생성하려는 직급
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hire_date,
            @RequestParam String status,
            @RequestParam(required = false) String dept_name,
            @RequestParam(required = false) String phone_number,
            @RequestParam(required = false) Integer supervisor,
            HttpSession session
    ) {
        try {
            Integer myId = (Integer) session.getAttribute("emp_id");
            if (myId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

            Employee me = employeeService.getEmployeeDetail(myId);
            if (me == null) return ResponseEntity.badRequest().body("사용자 정보를 찾을 수 없습니다.");

            // 1. 사원: 생성 불가
            if (POS_EMP.equals(me.getPosition())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 사원은 직원을 등록할 수 없습니다.");
            }

            // 2. 중간 관리자: '사원'만 생성 가능
            if (POS_MID.equals(me.getPosition())) {
                if (!POS_EMP.equals(position)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 중간 관리자는 오직 '사원' 직급만 생성할 수 있습니다.");
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

            boolean success = employeeService.addEmployee(newEmployee) > 0;
            if (success) {
                return ResponseEntity.ok("신규 직원이 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.internalServerError().body("DB 등록 실패: 입력 정보를 확인해주세요.");
            }

        } catch (Exception e) {
            log.error("직원 등록 실패", e);
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ================================================================
    // 2. 근태 관리
    // ================================================================

    @PostMapping("/attendance/generate")
    public ResponseEntity<String> generateDailyAttendance(HttpSession session) {
        Integer myId = (Integer) session.getAttribute("emp_id");
        if (myId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

        Employee me = employeeService.getEmployeeDetail(myId);
        if (POS_EMP.equals(me.getPosition())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 관리자만 근태를 생성할 수 있습니다.");
        }

        boolean success = attendanceService.generateDailyAttendance();
        if (success) return ResponseEntity.ok("일일 근태 기록이 생성되었습니다.");
        else return ResponseEntity.internalServerError().body("근태 생성 실패");
    }

    @GetMapping("/attendance")
    public List<Attendance> getAttendanceList(
            @RequestParam(required = false) String search_keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date) {
        return attendanceService.getAttendanceList(search_keyword, start_date, end_date);
    }

    @GetMapping("/attendance/{attendanceId}")
    public Attendance getAttendanceDetail(@PathVariable Integer attendanceId) {
        return attendanceService.getAttendanceDetail(attendanceId);
    }

    @PutMapping("/attendance/{attendanceId}")
    public ResponseEntity<String> updateAttendanceStatus(@PathVariable Integer attendanceId,
                                                         @RequestBody Attendance updatedAtt,
                                                         HttpSession session) {
        Integer myId = (Integer) session.getAttribute("emp_id");
        if (myId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

        Employee me = employeeService.getEmployeeDetail(myId);
        if (POS_EMP.equals(me.getPosition())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 관리자만 근태 상태를 수정할 수 있습니다.");
        }

        updatedAtt.setAttendance_id(attendanceId);
        boolean success = attendanceService.updateAttendanceStatus(updatedAtt);

        if (success) return ResponseEntity.ok("근태 상태가 수정되었습니다.");
        else return ResponseEntity.internalServerError().body("상태 수정 실패");
    }

    // ================================================================
    // 3. 결재 및 기타 로직
    // ================================================================

    private Long determineApprover(Employee me) {
        if (POS_TOP.equals(me.getPosition())) {
            return Long.valueOf(me.getEmp_id());
        }
        if (me.getSupervisor() != null) {
            return Long.valueOf(me.getSupervisor());
        }
        List<Employee> topManagers = employeeService.getEmployeeList(null, null, POS_TOP, null);
        if (topManagers != null && !topManagers.isEmpty()) {
            return Long.valueOf(topManagers.get(0).getEmp_id());
        }
        return null;
    }

    private int getRank(String position) {
        if (position == null) return 0;
        String pos = position.trim();
        if (POS_TOP.equals(pos)) return 3;
        if (POS_MID.equals(pos)) return 2;
        return 1;
    }

    @PostMapping("/requestVacation")
    public boolean requestVacation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason,
            HttpSession session) {
        try {
            Integer empIdObj = (Integer) session.getAttribute("emp_id");
            String empName = (String) session.getAttribute("emp_name");

            if (empIdObj == null) return false;
            Employee me = employeeService.getEmployeeDetail(empIdObj);

            if (empName == null) empName = me.getEmp_name();

            Long approverId = determineApprover(me);

            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 4);
            payload.put("tb_id", 0);
            payload.put("cd_id", 0);
            payload.put("requesterName", empName);
            payload.put("startDate", startDate.toString());
            payload.put("endDate", endDate.toString());
            payload.put("reason", reason);

            Documents doc = new Documents();
            doc.setTitle("[휴가신청] " + empName + " (" + startDate + " ~ " + endDate + ")");
            doc.setReq_id(Long.valueOf(empIdObj));
            doc.setReq_date(LocalDate.now());
            doc.setQuery(objectMapper.writeValueAsString(payload));
            doc.setStatus(0);
            doc.setAppr_id(approverId);
            doc.setCat_id(4);
            doc.setTb_id(0);
            doc.setCd_id(0);

            documentsService.addDocument(doc);
            return true;

        } catch (Exception e) {
            log.error("휴가 신청 실패", e);
            return false;
        }
    }



    @PostMapping("/employees/request-status-change")
    public ResponseEntity<String> requestStatusChange(@RequestBody Map<String, Object> reqData, HttpSession session) {
        try {
            Integer requesterId = (Integer) session.getAttribute("emp_id");
            if (requesterId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

            Employee me = employeeService.getEmployeeDetail(requesterId);
            Integer targetEmpId = Integer.parseInt(reqData.get("emp_id").toString());
            Employee target = employeeService.getEmployeeDetail(targetEmpId);

            if (target == null) return ResponseEntity.badRequest().body("대상 직원 정보를 찾을 수 없습니다.");

            // [권한 검증]
            // 1. 본인이 아닌 타인의 인사 변동을 신청하는 경우
            if (!requesterId.equals(targetEmpId)) {

                // (1) 사원은 타인 신청 불가
                if (POS_EMP.equals(me.getPosition())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 사원은 오직 본인의 인사 변동만 신청할 수 있습니다.");
                }

                // (2) 중간 관리자 권한 체크
                if (POS_MID.equals(me.getPosition())) {
                    // 2-1. 상위/동급 직급 신청 불가
                    if (getRank(target.getPosition()) >= getRank(me.getPosition())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 중간 관리자는 하위 직급(사원)에 대해서만 인사 변동을 신청할 수 있습니다.");
                    }

                    // 2-2. [추가] 타 부서 직원 신청 불가
                    if (!Objects.equals(me.getDept_name(), target.getDept_name())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음: 중간 관리자는 본인 소속 부서(" + me.getDept_name() + ")의 직원에 대해서만 신청할 수 있습니다.");
                    }
                }
            }
            // 2. 본인 신청은 허용 (결재선 로직으로 이동)

            Long approverId = determineApprover(me);

            String targetEmpName = (String) reqData.get("emp_name");
            String currentStatus = (String) reqData.get("currentStatus");
            String newStatus = (String) reqData.get("newStatus");
            String reason = (String) reqData.get("reason");

            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 4);
            payload.put("tb_id", 1);
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
            doc.setAppr_id(approverId);
            doc.setCat_id(4);
            doc.setTb_id(1);
            doc.setCd_id(1);

            documentsService.addDocument(doc);
            return ResponseEntity.ok("인사 발령 결재 요청이 전송되었습니다.");

        } catch (Exception e) {
            log.error("인사 발령 요청 실패", e);
            return ResponseEntity.internalServerError().body("요청 중 오류 발생");
        }
    }
}