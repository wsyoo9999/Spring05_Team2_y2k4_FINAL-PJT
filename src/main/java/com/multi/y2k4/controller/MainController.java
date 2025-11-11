package com.multi.y2k4.controller;



import com.multi.y2k4.service.db.DBService;
import com.multi.y2k4.service.db.TenantSchemaService;
import com.multi.y2k4.service.management.UserService;
import com.multi.y2k4.vo.user.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor

public class MainController {

    private final UserService userService;
    private final DBService dbService;
    private final TenantSchemaService tenantSchemaService;



    @GetMapping({"/"})
    public String index() {

        return "main";
    }

    @GetMapping("/login")
    public String login() {
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
            }
            httpSession.setAttribute("id", user.getId());
            httpSession.setAttribute("LOGIN_DB_NAME", dbName);
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
        int result = userService.addUser(userVO);
        if (result != 1){
            model.addAttribute("login_alert","로그인에 실패했습니다.");
        }
        else{
            model.addAttribute("login_alert","회원가입 성공, 로그인 해주세요");
        }
        return "login";
    }

    @GetMapping("/alerts")
    public String alerts(){
        return "alerts";

    }
}
