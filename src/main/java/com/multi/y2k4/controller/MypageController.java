package com.multi.y2k4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageController {

//     URL 요청 "/mypage"가 들어왔을 때 templates 폴더의 mypage.html을 렌더링한다.
//     @return 템플릿 파일 이름 -> "mypage"는 templates/mypage.html을 의미
//     복습

         @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }
}