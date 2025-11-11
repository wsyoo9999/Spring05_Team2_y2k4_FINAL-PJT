package com.multi.y2k4.interceptor;

import com.multi.y2k4.db.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;


//HandlerInterceptor는 특정한 URI 호출(컨트롤러 작동) 전과 후에 해야 할 일을 설정 가능
public class TenantContextInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //지정된 컨트롤러의 일이 끝났을 때 수행되는 일들
        TenantContext.clear();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("id") == null) { // 로그인이 안 되어 있으면
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            System.out.println("session is null");
            // JS로 alert 후 로그인 페이지로 이동
            String script = """
                    <script>
                        alert('로그인이 필요합니다.\\n로그인 페이지로 이동합니다.');
                        location.href = '/login';
                    </script>
                    """;

            response.getWriter().write(script);
            response.getWriter().flush();

            // 더 이상 컨트롤러로 진행하지 않음
            return false;
        }
        System.out.println("session is not null");
        String dbName = (String) session.getAttribute("LOGIN_DB_NAME");
        if (dbName != null) {
            TenantContext.setCurrentDb(dbName);
        }

        return true;
    }
}

