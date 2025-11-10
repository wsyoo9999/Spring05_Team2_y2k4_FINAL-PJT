package com.multi.y2k4.db;

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
        //지정된 컨트롤러 동작 전에 수행되야 할 일들
        HttpSession session = request.getSession(false);
        if (session != null) {
            String dbName = (String) session.getAttribute("LOGIN_DB_NAME");
            if (dbName != null) {
                TenantContext.setCurrentDb(dbName);
            }
        }

        return true; // 컨트롤러 진행
    }
}
