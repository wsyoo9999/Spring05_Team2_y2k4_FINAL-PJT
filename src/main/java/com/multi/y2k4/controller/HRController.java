package com.multi.y2k4.controller;

import com.multi.y2k4.vo.hr.Employee;
import com.multi.y2k4.vo.hr.Attendance; // 근태 VO import
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hr")
public class HRController {

    // ===========================================
    //         테스트용 더미 데이터 초기화
    // ===========================================
    private List<Employee> dummyEmployees = initializeDummyEmployees();
    private List<Attendance> dummyAttendance = initializeDummyAttendance();

    // 1. 직원 목록 조회 (검색 및 정렬 포함)
    @GetMapping("/employees")
    public List<Employee> getEmployeeList(
            @RequestParam(required = false) String search_keyword,
            @RequestParam(required = false, defaultValue = "emp_id,asc") String sort) {

        List<Employee> filteredList = dummyEmployees.stream()
                .filter(emp -> filterEmployee(emp, search_keyword))
                .collect(Collectors.toList());

        // 실제 DB 정렬 로직은 생략합니다.

        return filteredList;
    }

    // 2. 근태 현황 조회 API
    @GetMapping("/attendance")
    public List<Attendance> getAttendanceList(
            @RequestParam(required = false) String search_keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month_start_date) {

        // month_start_date를 기반으로 해당 월의 데이터만 필터링하는 로직이 필요하지만
        // 현재는 DB 연결 전이므로, 단순히 모든 더미 데이터를 이름/상태로 필터링합니다.

        List<Attendance> filteredList = dummyAttendance.stream()
                .filter(att -> filterAttendance(att, search_keyword))
                .collect(Collectors.toList());

        return filteredList;
    }

    // 3. 급여 대장 조회 API
    @GetMapping("/salary")
    public List<?> getSalaryList() {
        // 실제로는 급여 데이터 반환
        return List.of("Salary data goes here");
    }

    // ===========================================
    //         테스트용 데이터 생성 로직
    // ===========================================

    private List<Employee> initializeDummyEmployees() {
        List<Employee> list = new ArrayList<>();

        Employee e1 = new Employee();
        e1.setEmp_id(2024001);
        e1.setEmp_name("홍길동");
        e1.setDept_name("인사팀");
        e1.setPosition("팀장");
        e1.setHire_date(LocalDate.of(2020, 3, 1));
        e1.setStatus("재직");
        e1.setPhone_number("010-1111-2222");
        list.add(e1);

        Employee e2 = new Employee();
        e2.setEmp_id(2024002);
        e2.setEmp_name("김철수");
        e2.setDept_name("생산1팀");
        e2.setPosition("사원");
        e2.setHire_date(LocalDate.of(2023, 8, 15));
        e2.setStatus("재직");
        e2.setPhone_number("010-3333-4444");
        list.add(e2);

        Employee e3 = new Employee();
        e3.setEmp_id(2024003);
        e3.setEmp_name("이영희");
        e3.setDept_name("회계팀");
        e3.setPosition("대리");
        e3.setHire_date(LocalDate.of(2022, 1, 20));
        e3.setStatus("재직");
        e3.setPhone_number("010-5555-6666");
        list.add(e3);

        Employee e4 = new Employee();
        e4.setEmp_id(2024004);
        e4.setEmp_name("박민지");
        e4.setDept_name("영업부");
        e4.setPosition("과장");
        e4.setHire_date(LocalDate.of(2018, 5, 10));
        e4.setStatus("휴직");
        e4.setPhone_number("010-7777-8888");
        list.add(e4);

        return list;
    }

    private List<Attendance> initializeDummyAttendance() {
        List<Attendance> list = new ArrayList<>();
        LocalDate today = LocalDate.of(2025, 11, 4);

        // 홍길동 (정상 근무)
        Attendance a1 = new Attendance();
        a1.setAttendance_id(1);
        a1.setEmp_id(2024001);
        a1.setEmp_name("홍길동");
        a1.setAtt_date(today);
        a1.setCheck_in(LocalTime.of(8, 55));
        a1.setCheck_out(LocalTime.of(18, 5));
        a1.setStatus("정상");
        list.add(a1);

        // 김철수 (지각)
        Attendance a2 = new Attendance();
        a2.setAttendance_id(2);
        a2.setEmp_id(2024002);
        a2.setEmp_name("김철수");
        a2.setAtt_date(today);
        a2.setCheck_in(LocalTime.of(9, 15));
        a2.setCheck_out(LocalTime.of(18, 0));
        a2.setStatus("지각");
        list.add(a2);

        // 이영희 (조퇴)
        Attendance a3 = new Attendance();
        a3.setAttendance_id(3);
        a3.setEmp_id(2024003);
        a3.setEmp_name("이영희");
        a3.setAtt_date(today);
        a3.setCheck_in(LocalTime.of(8, 50));
        a3.setCheck_out(LocalTime.of(16, 30));
        a3.setStatus("조퇴");
        list.add(a3);

        // 박민지 (결근) - 11월 3일 데이터
        Attendance a4 = new Attendance();
        a4.setAttendance_id(4);
        a4.setEmp_id(2024004);
        a4.setEmp_name("박민지");
        a4.setAtt_date(today.minusDays(1)); // 어제 날짜
        a4.setCheck_in(null);
        a4.setCheck_out(null);
        a4.setStatus("결근");
        list.add(a4);

        return list;
    }

    // ===========================================
    //         필터링 유틸리티 로직
    // ===========================================

    private boolean filterEmployee(Employee emp, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lowerCaseKeyword = keyword.trim().toLowerCase();

        return (emp.getEmp_name() != null && emp.getEmp_name().toLowerCase().contains(lowerCaseKeyword)) ||
                (emp.getDept_name() != null && emp.getDept_name().toLowerCase().contains(lowerCaseKeyword)) ||
                (emp.getPosition() != null && emp.getPosition().toLowerCase().contains(lowerCaseKeyword));
    }

    private boolean filterAttendance(Attendance att, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lowerCaseKeyword = keyword.trim().toLowerCase();

        // 이름, 상태에 대해 검색
        return (att.getEmp_name() != null && att.getEmp_name().toLowerCase().contains(lowerCaseKeyword)) ||
                (att.getStatus() != null && att.getStatus().toLowerCase().contains(lowerCaseKeyword));
    }
}