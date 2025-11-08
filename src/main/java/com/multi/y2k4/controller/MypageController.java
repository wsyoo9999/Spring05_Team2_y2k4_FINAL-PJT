package com.multi.y2k4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageController {

//     URL 요청 "/mypage"가 들어왔을 때 templates 폴더의 mypage.html을 렌더링한다.
//     @return 템플릿 파일 이름 -> "mypage"는 templates/mypage.html을 의미
//     복습
//     마이페이지 조회 화면 (읽기 전용 상태), Renders: templates/mypage.html
         @GetMapping("/mypage")
    public String mypage() {
        return "mypage";

    }

//     마이페이지 수정 화면 매핑(수정 가능 상태)
//     templates/mypageedit.html

    @GetMapping("/mypageedit")
    public String mypageEdit() {
//         templates/mypageedit.html 파일을 렌더링합니다.
        return "mypageedit";
    }
}