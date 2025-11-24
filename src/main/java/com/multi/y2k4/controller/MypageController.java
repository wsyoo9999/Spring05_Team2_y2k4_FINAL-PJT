package com.multi.y2k4.controller;

import com.multi.y2k4.service.management.UserService;
import com.multi.y2k4.vo.user.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MypageController {
    private final UserService userService;


    //     URL 요청 "/mypage"가 들어왔을 때 templates 폴더의 mypage.html을 렌더링한다.
//     @return 템플릿 파일 이름 -> "mypage"는 templates/mypage.html을 의미
//     복습
//     마이페이지 조회 화면 (읽기 전용 상태), Renders: templates/mypage.html
         @GetMapping("/mypage")
    public String mypage() {
        return "mypage";

    }

    @GetMapping("/mypage/inform")
    @ResponseBody
    public Map<String, Object> getMypageInfo(HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        String loginId = (String) session.getAttribute("id");
        if (loginId == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        UserVO user = userService.selectById(loginId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "사용자 정보를 찾을 수 없습니다.");
            return result;
        }

        result.put("success", true);

        // 기본 정보
        result.put("name", user.getName());
        result.put("birthday", user.getBirthday());     // LocalDate → JSON "yyyy-MM-dd"
        result.put("userId", user.getId());             // 로그인 아이디
        result.put("email", user.getEmail());
        result.put("contact", user.getPhone());         // 연락처
        result.put("companyId", user.getCompany_id());  // 사업자 번호 / 회사 코드

        // 인사 정보 (조인 결과)
        result.put("empId", user.getEmp_id());          // 사원번호
        result.put("dept", user.getDept_name());         // 부서명 (dept_name 컬럼)
        result.put("position", user.getPosition());     // 직급 (hr.position)


        return result;
    }
//     마이페이지 수정 화면 매핑(수정 가능 상태)
//     templates/mypageedit.html

    @GetMapping("/mypageedit")
    public String mypageEdit() {
//         templates/mypageedit.html 파일을 렌더링합니다.
        return "mypageedit";
    }

    @PostMapping("/mypageedit/edit")
    @ResponseBody
    public Map<String, Object> updateProfile(
            HttpSession session,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String contact,
            @RequestParam(name = "current_password", required = false) String currentPassword,
            @RequestParam(name = "new_password", required = false) String newPassword,
            @RequestParam(name = "confirm_password", required = false) String confirmPassword
    ) {
        Map<String, Object> result = new HashMap<>();

        // 1) 로그인 여부 확인 (세션에서 id 가져오기)
        String loginId = (String) session.getAttribute("id");
        if (loginId == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        // 2) 비밀번호 변경 의사가 있는지 체크 (새 비밀번호 입력 여부)
        boolean changePassword = (newPassword != null && !newPassword.isBlank());

        if (changePassword) {
            // 2-1) 새 비밀번호와 확인 비밀번호 일치 여부
            if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
                result.put("success", false);
                result.put("message", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
                return result;
            }

            // 2-2) 현재 비밀번호 입력 여부
            if (currentPassword == null || currentPassword.isBlank()) {
                result.put("success", false);
                result.put("message", "비밀번호를 변경하려면 현재 비밀번호를 입력해야 합니다.");
                return result;
            }
        } else {
            // 비밀번호 변경 안 하는 경우 → 서비스에 null로 넘김
            currentPassword = null;
            newPassword = null;
        }

        int updated = userService.updateMypage(
                loginId,
                name,
                email,
                contact,        // contact -> phone 으로 매핑
                currentPassword,
                newPassword
        );

        // 4) 결과 처리
        if (changePassword) {
            // 비밀번호 변경을 시도한 경우
            if (updated == 0) {
                // WHERE에서 password = currentPassword 조건이 안 맞은 경우
                result.put("success", false);
                result.put("message", "현재 비밀번호가 일치하지 않습니다.");
                return result;
            }
        } else {
            // 비밀번호 변경 없이 단순 프로필 수정인 경우
            if (updated == 0) {
                // 이 경우는 값이 동일해서 실제로 수정된 행이 없을 수도 있음
                // 그래도 에러는 아니라고 보고 성공 처리해도 됨
            }
        }

        result.put("success", true);
        result.put("message", "회원 정보가 수정되었습니다.");
        return result;
    }
}