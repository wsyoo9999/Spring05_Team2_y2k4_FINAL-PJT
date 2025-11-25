package com.multi.y2k4.interceptor;

import com.multi.y2k4.db.TenantContext;
import jakarta.servlet.DispatcherType;
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

        String uri = request.getRequestURI();
        DispatcherType dispatcherType = request.getDispatcherType();

        if (dispatcherType == DispatcherType.ERROR || dispatcherType == DispatcherType.ASYNC) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String dbName = (String) session.getAttribute("LOGIN_DB_NAME");
                if (dbName != null) {
                    TenantContext.setCurrentDb(dbName);
                }
            }
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("id") == null) {
            System.out.println("session is null, uri = " + uri);


            if (uri.startsWith("/sse/")) {

                return false;
            }


            if (!response.isCommitted()) {
                response.sendRedirect("/login?needLogin=true");
            }
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
