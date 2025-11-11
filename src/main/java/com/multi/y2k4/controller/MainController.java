package com.multi.y2k4.controller;


import com.multi.y2k4.mapper.management.UserMapper;
import com.multi.y2k4.mapper.tenant.db.DatabaseMapper;
import com.multi.y2k4.service.TenantSchemaService;
import com.multi.y2k4.vo.user.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class MainController {

    private final UserMapper userMapper;
    private final DatabaseMapper databaseMapper;
    private final TenantSchemaService tenantSchemaService;

    public MainController(UserMapper userMapper, DatabaseMapper databaseMapper, TenantSchemaService tenantSchemaService) {
        this.userMapper = userMapper;
        this.databaseMapper = databaseMapper;
        this.tenantSchemaService = tenantSchemaService;
    }

    @GetMapping({"/"})
    public String index() {
        //=======================================================
        String id = "admin";
        String password = "1234";

        // 1) 로그인 시도
        UserVO user = userMapper.checkLogin(id, password);

        if (user == null) {
            return "로그인 실패: admin / 1234 계정을 user 테이블에 먼저 넣어주세요.";
        }

        // 2) 사용할 DB 이름 결정
        String dbName = user.getCompany_id();

        // dbName 이 null 이거나 비어있으면 임시로 하나 지정 (테스트용)
        if (dbName == null || dbName.isBlank()) {
            dbName = "admin_db"; // 또는 company_code 기반 규칙 등
        }

        // 3) DB 존재 여부 확인
        int exists = databaseMapper.existsDatabase(dbName);

        StringBuilder sb = new StringBuilder();
        sb.append("로그인 성공: ").append(user.getId()).append("<br>");
        sb.append("사용할 DB 이름: ").append(dbName).append("<br>");

        if (exists == 0) {
            // 4) DB 없으면 생성
            databaseMapper.createDatabase(dbName);
            tenantSchemaService.migrate(dbName);
            sb.append("DB가 존재하지 않아 새로 생성했습니다.<br>");
        } else {
            sb.append("DB가 이미 존재합니다.<br>");
        }
        System.out.println(sb.toString());
        //======================================================
        return "main";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/addUser")
    public String addUser() {
        return "addUser";
    }

    @GetMapping("/alerts")
    public String alerts(){
        return "alerts";
    }
}
