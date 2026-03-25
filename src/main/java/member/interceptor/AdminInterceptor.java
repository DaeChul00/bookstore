package member.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import member.model.MemberVO;

public class AdminInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 세션에서 로그인 정보를 꺼냅니다.
        HttpSession session = request.getSession();
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

        // 2. 로그인이 안 되어 있거나, 권한이 ADMIN이 아니라면?
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            
            // 경고 메시지를 띄우고 메인으로 돌려보냅니다.
            // (간단하게 리다이렉트만 처리하거나 스크립트를 활용할 수 있습니다.)
            response.sendRedirect(request.getContextPath() + "/?error=auth");
            return false; // 컨트롤러로 못 가게 막음!
        }

        // 3. 관리자가 맞다면 통과!
        return true;
    }
}