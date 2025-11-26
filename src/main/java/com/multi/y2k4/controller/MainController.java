package com.multi.y2k4.controller;


import com.multi.y2k4.db.TenantContext;
import com.multi.y2k4.service.alert.AlertService;
import com.multi.y2k4.service.db.DBService;
import com.multi.y2k4.service.db.TenantSchemaService;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.hr.EmployeeService;
import com.multi.y2k4.service.management.UserService;
import com.multi.y2k4.sse.EmitterRepository;
import com.multi.y2k4.sse.SseEmitterService;
import com.multi.y2k4.vo.alert.Alert;
import com.multi.y2k4.vo.hr.Employee;
import com.multi.y2k4.vo.user.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final DBService dbService;
    private final TenantSchemaService tenantSchemaService;
    private final EmployeeService employeeService;
    private final SseEmitterService sseEmitterService;
    private final AlertService alertService;
    private final DocumentsService documentsService;
    private final EmitterRepository emitterRepository;


    @GetMapping({"/"})
    public String index(HttpSession httpSession) {
        //추후 httpSession.getAttribute("id");를 통해 현재 로그인중인 사람의 정보 가져오는 기능 필요

        Object empObj = httpSession.getAttribute("emp_id");
        if (empObj instanceof Integer empId) {
            alertService.notifyDocCountChanged(empId.longValue());
        } else {
            // 로그인 안 된 상태거나 아직 emp_id가 없는 경우
            System.out.println("emp_id not found in session, skip SSE notify");
        }

        return "main";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "needLogin", required = false) String needLogin,  Model model) {
        if (needLogin != null) {
            model.addAttribute("login_alert", "로그인이 필요합니다.\\n로그인 페이지로 이동합니다.");
        }
        return "login";
    }

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


            // 3) DB 존재 여부 확인
            int exists = dbService.existsDatabase(dbName);

            if (exists == 0) {
                // 4) DB 없으면 생성
                dbService.createDatabase(dbName);
                // 4-1) 테이블도 생성
                tenantSchemaService.migrate(dbName);
                System.out.println(dbName + " 데이터베이스가 존재하지 않아 새로 생성했습니다.");
            }

            // 5) 세션에 정보 등록
            httpSession.setAttribute("id", user.getId());
            httpSession.setAttribute("LOGIN_DB_NAME", dbName);

            TenantContext.setCurrentDb(dbName);

            Employee me = employeeService.getEmployeeByLoginId(user.getId());

            if (me != null) {
                httpSession.setAttribute("emp_id", me.getEmp_id());        // 내 사번
                httpSession.setAttribute("position", me.getPosition());    // 내 직급/권한
                httpSession.setAttribute("supervisor", me.getSupervisor()); // 내 상급자


                System.out.println("=== 로그인 세션 세팅 ===");
                System.out.println("emp_id      = " + httpSession.getAttribute("emp_id"));
                System.out.println("position    = " + httpSession.getAttribute("position"));
                System.out.println("supervisor  = " + httpSession.getAttribute("supervisor"));
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
                          @RequestParam("company_id") String company_id,
                          @RequestParam("birthday") LocalDate birthday,
                          @RequestParam("phone") String phone,
                          Model model){

        UserVO userVO = new UserVO();
        userVO.setId(id);
        userVO.setPassword(password);
        userVO.setEmail(email);
        userVO.setName(name);
        userVO.setCompany_id(company_id);
        userVO.setBirthday(birthday);
        userVO.setPhone(phone);
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
            newEmployee.setLogin_id(userVO.getId());
            newEmployee.setStatus("재직"); // 재직 상태
            newEmployee.setPhone_number(userVO.getPhone());
            newEmployee.setPosition(defaultPosition); // 첫 사용자인지 여부에 따라 직급 부여

            // 7) 해당 테넌트 DB의 인사(human_resource) 테이블에 추가
            employeeService.addEmployee(newEmployee);

            if(exists==0){
                int emp_id = newEmployee.getEmp_id();
                newEmployee.setSupervisor(emp_id);
                employeeService.updateEmployee(newEmployee);
            }

        } catch (Exception e) {
            // 직원 등록에 실패하더라도 사용자 계정은 생성된 상태
            System.err.println("회원가입은 성공했으나, 테넌트 DB 직원 등록 실패: " + e.getMessage());
        } finally {
        }

        model.addAttribute("login_alert","회원가입 성공, 로그인 해주세요");
        return "login";
    }

    @GetMapping("/check-id")
    @ResponseBody
    public boolean checkId(@RequestParam("id") String id) {
        return userService.existsById(id);
    }

    @GetMapping("/me")
    @ResponseBody
    public Map<String, Object> getCurrentUser(HttpSession session) {
        String loginId = (String) session.getAttribute("id");
        Map<String, Object> result = new HashMap<>();

        UserVO user = userService.selectById(loginId);


        String position = (String) session.getAttribute("position");
        result.put("position", position);

        if (user != null) {
            result.put("name", user.getName());
        } else {
            result.put("name", "");
        }

        return result;
    }

    @GetMapping("/alerts")
    public String alertsPage() {
        return "alerts";  // alerts.html 반환
    }

    // 2. JSON 데이터 반환
//    @GetMapping("/api/alerts")
//    @ResponseBody
//    public List<Alert> getMyAlerts(HttpSession session) {
//        Integer empIdInt = (Integer) session.getAttribute("emp_id");
//
//        if (empIdInt == null) {
//            return new ArrayList<>();
//        }
//
//        Long emp_id = empIdInt.longValue();
//
//        return alertService.selectAlerts(emp_id);
//    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        Object empObj = httpSession.getAttribute("emp_id");
        Long empId = null;

        if (empObj instanceof Integer i) {
            empId = i.longValue();
        } else if (empObj instanceof Long l) {
            empId = l;
        }

        if (empId != null) {
            if (emitterRepository.findById(empId) != null) {
                sseEmitterService.unsubscribe(empId);
            }
        }

        httpSession.invalidate();
        return "redirect:/login";
    }
    @GetMapping({"/document"})
    public String document() {

        return "approval";
    }
}