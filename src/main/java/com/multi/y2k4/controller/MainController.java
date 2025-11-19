package com.multi.y2k4.controller;


import com.multi.y2k4.db.TenantContext;
import com.multi.y2k4.service.db.DBService;
import com.multi.y2k4.service.db.TenantSchemaService;
import com.multi.y2k4.service.hr.EmployeeService;
import com.multi.y2k4.service.management.UserService;
import com.multi.y2k4.vo.hr.Employee;
import com.multi.y2k4.vo.user.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final DBService dbService;
    private final TenantSchemaService tenantSchemaService;
    private final EmployeeService employeeService;



    @GetMapping({"/"})
    public String index(HttpSession httpSession) {
        //추후 httpSession.getAttribute("id");를 통해 현재 로그인중인 사람의 정보 가져오는 기능 필요
        httpSession.setAttribute("me", 1);
        httpSession.setAttribute("supervisor", 2);
        httpSession.setAttribute("authLevel", 1);
        return "main";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ▼ [수정] login 메소드
    @PostMapping("/login")
    public String login(@RequestParam("id") String id, @RequestParam("password") String password, HttpSession httpSession, Model model) {
        UserVO user = userService.checkLogin(id, password);
        if (user == null) { //로그인 실패
            model.addAttribute("login_alert","로그인에 실패했습니다.");
            return "login";
        } else {
            String dbName = user.getCompany_id();
            // dbName 이 null 이거나 비어있으면 임시로 하나 지정 (테스트용)
            dbName = "db_" + dbName;

            // 3) DB 존재 여부 확인 (DB가 없으면 생성 및 마이그레이션)
            //    (회원가입 시 이미 생성되었어야 하지만, 안전장치로 확인)
            int exists = dbService.existsDatabase(dbName);

            if (exists == 0) {
                // 4) DB 없으면 생성 (직원 등록은 회원가입시 처리)
                dbService.createDatabase(dbName);
                // 4-1) 테이블 생성
                tenantSchemaService.migrate(dbName);
                System.out.println(dbName + " 데이터베이스가 존재하지 않아 새로 생성했습니다.");
            }

            // 5) 세션에 정보 등록
            httpSession.setAttribute("id", user.getId());
            httpSession.setAttribute("LOGIN_DB_NAME", dbName);

            TenantContext.setCurrentDb(dbName);

            List<Employee> empList = employeeService.getEmployeeList(user.getName(), null, null, null);
            //같은 이름 처리?
            if (!empList.isEmpty()) {
                Employee me = empList.get(0);
                httpSession.setAttribute("emp_id", me.getEmp_id());     // 내 사번
                httpSession.setAttribute("position", me.getPosition()); // 내 직급
                httpSession.setAttribute("emp_name", me.getEmp_name()); // 내 이름

            } else {

            }
            return "redirect:/";
        }
    }

    @GetMapping("/addUser")
    public String addUser() {
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam("id") String id,
                          @RequestParam("password") String password,
                          @RequestParam("email") String email,
                          @RequestParam("name") String name,
                          @RequestParam("company_id") String company_id, Model model){


        UserVO userVO = new UserVO();
        userVO.setId(id);
        userVO.setPassword(password);
        userVO.setEmail(email);
        userVO.setCompany_id(company_id);
        userVO.setName(name);

        // 1. 관리 DB(userDB)에 사용자 추가
        int result = userService.addUser(userVO);

        if (result != 1){
            model.addAttribute("login_alert","회원가입에 실패했습니다.");
            return "login";
        }

        // 2. 테넌트 DB 준비 및 직원 정보 추가
        try {
            String dbName = "db_" + userVO.getCompany_id();

            // 3) DB 존재 여부 확인
            int exists = dbService.existsDatabase(dbName);
            String defaultPosition = "사원"; // 기본값: 일반 사원

            if (exists == 0) {
                // 4) DB 없으면 생성 (이 사용자가 이 회사의 첫 가입자)
                dbService.createDatabase(dbName);
                // 4-1) 테이블도 생성
                tenantSchemaService.migrate(dbName);
                defaultPosition = "최상위 관리자"; // 이 회사의 첫 가입자이므로 '관리자' 부여
            }

            // 5) EmployeeService가 올바른 DB를 바라보도록 현재 스레드에 DB 이름 설정
            TenantContext.setCurrentDb(dbName);

            // 6) 인사 테이블에 등록할 Employee 객체 생성
            Employee newEmployee = new Employee();
            newEmployee.setEmp_name(userVO.getName()); // 회원가입 시 입력한 이름 사용
            newEmployee.setHire_date(LocalDate.now()); // 오늘 날짜로 자동 입사 처리
            newEmployee.setStatus("재직"); // 재직 상태
            newEmployee.setPosition(defaultPosition); // 첫 사용자인지 여부에 따라 직급 부여

            // 7) 해당 테넌트 DB의 인사(human_resource) 테이블에 추가
            employeeService.addEmployee(newEmployee);

        } catch (Exception e) {
            // 직원 등록에 실패하더라도 사용자 계정은 생성된 상태
            System.err.println("회원가입은 성공했으나, 테넌트 DB 직원 등록 실패: " + e.getMessage());
        } finally {
        }

        model.addAttribute("login_alert","회원가입 성공, 로그인 해주세요");
        return "login";
    }

    @GetMapping("/alerts")
    public String alerts(){
        return "alerts";

    }
    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate(); // 세션 만료
        return "redirect:/login";
    }
    @GetMapping({"/document"})
    public String document() {

        return "approval";
    }
}