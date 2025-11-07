package com.multi.y2k4.controller;

import com.multi.y2k4.vo.hr.Employee;
import com.multi.y2k4.vo.hr.Attendance;
import com.multi.y2k4.vo.hr.Salary;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hr")
public class HRController {


    private List<Employee> dummyEmployees = initializeDummyEmployees();
    private List<Attendance> dummyAttendance = initializeDummyAttendance();
    private List<Salary> dummySalary = initializeDummySalary();

    // 1. 직원 목록 조회
    @GetMapping("/employees")
    public List<Employee> getEmployeeList(
            @RequestParam(required = false) String search_name,
            @RequestParam(required = false) String search_dept,
            @RequestParam(required = false) String search_position,
            @RequestParam(required = false, defaultValue = "emp_id,asc") String sort) {

        List<Employee> filteredList = dummyEmployees.stream()
                .filter(emp -> filterEmployee(emp, search_name, search_dept, search_position))
                .collect(Collectors.toList());


        return filteredList;
    }

    // 5. 직원 상세 조회 API (GET, /api/hr/employees/{empId})
    @GetMapping("/employees/{empId}")
    public Employee getEmployeeDetail(@PathVariable Integer empId) {
        // 더미 데이터에서 ID가 일치하는 직원 객체를 찾아 반환
        return dummyEmployees.stream()
                .filter(e -> e.getEmp_id().equals(empId))
                .findFirst()
                .orElse(null);
    }

    // 6. 직원 정보 수정 API (PUT, /api/hr/employees/{empId})
    @PutMapping("/employees/{empId}")
    public boolean updateEmployee(@PathVariable Integer empId, @RequestBody Employee updatedEmp) {


        Employee target = dummyEmployees.stream()
                .filter(e -> e.getEmp_id().equals(empId))
                .findFirst()
                .orElse(null);

        if (target != null) {
            // 부서, 직급, 상태를 수정
            target.setDept_name(updatedEmp.getDept_name());
            target.setPosition(updatedEmp.getPosition());
            target.setStatus(updatedEmp.getStatus());

            System.out.println("직원 ID " + empId + " 정보 수정됨: 부서=" + updatedEmp.getDept_name() + ", 직급=" + updatedEmp.getPosition() + ", 상태=" + updatedEmp.getStatus());
            return true;
        }

        return false;
    }

    // 2. 근태 현황 조회
    @GetMapping("/attendance")
    public List<Attendance> getAttendanceList(
            @RequestParam(required = false) String search_keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date) {

        List<Attendance> filteredList = dummyAttendance.stream()
                .filter(att -> filterAttendance(att, search_keyword))
                .filter(att -> filterAttendanceByDate(att, start_date, end_date))
                .collect(Collectors.toList());

        return filteredList;
    }

    // 7. 근태 기록 상세 조회 API (GET, /api/hr/attendance/{id})
    @GetMapping("/attendance/{attendanceId}")
    public Attendance getAttendanceDetail(@PathVariable Integer attendanceId) {
        // 더미 데이터에서 ID가 일치하는 근태 객체를 찾아 반환
        return dummyAttendance.stream()
                .filter(a -> a.getAttendance_id().equals(attendanceId))
                .findFirst()
                .orElse(null);
    }

    // 8. 근태 기록 상태 수정 API (PUT, /api/hr/attendance/{id})
    @PutMapping("/attendance/{attendanceId}")
    public boolean updateAttendanceStatus(@PathVariable Integer attendanceId, @RequestBody Attendance updatedAtt) {

        Attendance target = dummyAttendance.stream()
                .filter(a -> a.getAttendance_id().equals(attendanceId))
                .findFirst()
                .orElse(null);

        if (target != null) {
            // 상태만 수정 (가장 간단한 제안 2 방식)
            target.setAttendance_status(updatedAtt.getAttendance_status());

            System.out.println("근태 ID " + attendanceId + " 상태 수정됨: " + updatedAtt.getAttendance_status());
            return true;
        }

        return false;
    }

    // 3. 급여 대장 조회 API
    @GetMapping("/salary")
    public List<Salary> getSalaryList(
            @RequestParam(required = false) Integer search_year,
            @RequestParam(required = false) Integer search_month) {

        if (search_year != null || search_month != null) {
            return dummySalary.stream()
                    .filter(s -> filterSalary(s, search_year, search_month))
                    .collect(Collectors.toList());
        }

        return dummySalary;
    }

    // 4. 급여 상세 조회 API
    @GetMapping("/salary/{salaryId}")
    public Salary getSalaryDetail(@PathVariable Integer salaryId) {
        return dummySalary.stream()
                .filter(s -> s.getSalary_id().equals(salaryId))
                .findFirst()
                .orElse(null);
    }


    // ================================================================
    // 더미 데이터 초기화 및 유틸리티 로직
    // ================================================================

    private List<Employee> initializeDummyEmployees() {
        List<Employee> list = new ArrayList<>();

        Employee e1 = new Employee(); e1.setEmp_id(2024001); e1.setEmp_name("홍길동"); e1.setDept_name("인사팀"); e1.setPosition("팀장"); e1.setHire_date(LocalDate.of(2020, 3, 1)); e1.setStatus("재직"); e1.setPhone_number("010-1111-2222"); list.add(e1);
        Employee e2 = new Employee(); e2.setEmp_id(2024002); e2.setEmp_name("김철수"); e2.setDept_name("생산1팀"); e2.setPosition("사원"); e2.setHire_date(LocalDate.of(2023, 8, 15)); e2.setStatus("재직"); e2.setPhone_number("010-3333-4444"); list.add(e2);
        Employee e3 = new Employee(); e3.setEmp_id(2024003); e3.setEmp_name("이영희"); e3.setDept_name("회계팀"); e3.setPosition("대리"); e3.setHire_date(LocalDate.of(2022, 1, 20)); e3.setStatus("재직"); e3.setPhone_number("010-5555-6666"); list.add(e3);
        Employee e4 = new Employee(); e4.setEmp_id(2024004); e4.setEmp_name("박민지"); e4.setDept_name("영업부"); e4.setPosition("과장"); e4.setHire_date(LocalDate.of(2018, 5, 10)); e4.setStatus("휴직"); e4.setPhone_number("010-7777-8888"); list.add(e4);

        return list;
    }

    private List<Attendance> initializeDummyAttendance() {
        List<Attendance> list = new ArrayList<>();
        LocalDate today = LocalDate.of(2025, 11, 5);

        // 🚩 createAttendance 함수 시그니처 변경에 맞춰 인자 수정
        list.add(createAttendance(1, 2024001, "홍길동", today, 8, 55, 18, 5, "정상"));
        list.add(createAttendance(2, 2024002, "김철수", today, 9, 15, 18, 0, "지각"));
        list.add(createAttendance(3, 2024003, "이영희", today.minusDays(1), 8, 50, 16, 30, "조퇴"));
        list.add(createAttendance(4, 2024004, "박민지", today.minusDays(2), 0, 0, 0, 0, "결근"));
        list.add(createAttendance(5, 2024001, "홍길동", today.minusDays(3), 8, 55, 18, 5, "정상"));
        list.add(createAttendance(6, 2024002, "김철수", today.minusDays(30), 8, 40, 19, 0, "정상"));

        return list;
    }

    // 🚩 createAttendance 함수 시그니처 변경 (overtime_hours 제거)
    private Attendance createAttendance(int id, int emp_id, String emp_name, LocalDate date, int inH, int inM, int outH, int outM, String status) {
        Attendance a = new Attendance();
        a.setAttendance_id(id);
        a.setEmp_id(emp_id);
        a.setEmp_name(emp_name);
        a.setWork_date(date);

        if (inH != 0) {
            a.setCheck_in(LocalDateTime.of(date, java.time.LocalTime.of(inH, inM, 0)));
        }
        if (outH != 0) {
            a.setCheck_out(LocalDateTime.of(date, java.time.LocalTime.of(outH, outM, 0)));
        }
        a.setAttendance_status(status);
        // a.setOvertime_hours(null); // VO에서 필드를 삭제했으므로 호출 필요 없음

        return a;
    }

    private List<Salary> initializeDummySalary() {
        List<Salary> list = new ArrayList<>();

        list.add(createSalary(1, 2024001, "홍길동", 2025, 10, 4000000, 300000, 600000, "국민은행", "111-222-333333"));
        list.add(createSalary(2, 2024002, "김철수", 2025, 10, 2800000, 200000, 450000, "신한은행", "444-555-666666"));
        list.add(createSalary(3, 2024003, "이영희", 2025, 10, 3200000, 250000, 500000, "우리은행", "777-888-999999"));

        list.add(createSalary(4, 2024001, "홍길동", 2025, 9, 4000000, 300000, 600000, "국민은행", "111-222-333333"));
        list.add(createSalary(5, 2024002, "김철수", 2025, 9, 2800000, 200000, 450000, "신한은행", "444-555-666666"));

        list.add(createSalary(6, 2024003, "이영희", 2024, 12, 3200000, 250000, 500000, "우리은행", "777-888-999999"));

        return list;
    }

    private Salary createSalary(int id, int emp_id, String emp_name, int year, int month, int basic_salary, int allowance, int deduction_amount, String bank_name, String account_number) {
        Salary s = new Salary();
        s.setSalary_id(id);
        s.setEmp_id(emp_id);
        s.setEmp_name(emp_name);
        s.setPayment_date(LocalDate.of(year, month, 25));
        s.setBasic_salary(basic_salary);
        s.setAllowance(allowance);

        int total_gross = basic_salary + allowance;
        s.setTotal_gross(total_gross);

        s.setDeduction_amount(deduction_amount);

        s.setTotal_pay(total_gross - deduction_amount);
        s.setBank_name(bank_name);
        s.setAccount_number(account_number);

        return s;
    }

    private boolean filterEmployee(Employee emp, String name, String dept, String position) {

        if (name != null && !name.trim().isEmpty()) {
            String lowerCaseName = name.trim().toLowerCase();
            if (emp.getEmp_name() == null || !emp.getEmp_name().toLowerCase().contains(lowerCaseName)) {
                return false;
            }
        }

        if (dept != null && !dept.trim().isEmpty()) {
            String lowerCaseDept = dept.trim().toLowerCase();
            if (emp.getDept_name() == null || !emp.getDept_name().toLowerCase().contains(lowerCaseDept)) {
                return false;
            }
        }

        if (position != null && !position.trim().isEmpty()) {
            String lowerCasePosition = position.trim().toLowerCase();
            if (emp.getPosition() == null || !emp.getPosition().toLowerCase().contains(lowerCasePosition)) {
                return false;
            }
        }

        return true;
    }

    private boolean filterAttendance(Attendance att, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lowerCaseKeyword = keyword.trim().toLowerCase();

        return (att.getEmp_name() != null && att.getEmp_name().toLowerCase().contains(lowerCaseKeyword)) ||
                (att.getAttendance_status() != null && att.getAttendance_status().toLowerCase().contains(lowerCaseKeyword));
    }

    private boolean filterAttendanceByDate(Attendance att, LocalDate startDate, LocalDate endDate) {
        LocalDate workDate = att.getWork_date();

        if (workDate == null) {
            return true;
        }

        if (startDate != null && workDate.isBefore(startDate)) {
            return false;
        }

        if (endDate != null && workDate.isAfter(endDate)) {
            return false;
        }

        return true;
    }

    private boolean filterSalary(Salary salary, Integer year, Integer month) {
        LocalDate paymentDate = salary.getPayment_date();
        if (paymentDate == null) {
            return false;
        }

        if (year != null && paymentDate.getYear() != year) {
            return false;
        }

        if (month != null && paymentDate.getMonthValue() != month) {
            return false;
        }

        return true;
    }
}