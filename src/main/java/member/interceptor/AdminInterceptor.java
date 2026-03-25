package member.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import member.model.MemberVO;

public class AdminInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 세션에서 로그인 정보를 가져옵니다.
        HttpSession session = request.getSession();
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

        // 2. 로그인이 되어 있지 않거나, 권한이 'ADMIN'이 아니라면 차단합니다.
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            
            // 권한 부족 메시지와 함께 메인 페이지로 리다이렉트합니다.
            // (컨트롤러에서 이 파라미터를 받아 alert창을 띄울 수 있습니다.)
            response.sendRedirect(request.getContextPath() + "/?error=auth");
            
            return false; // 컨트롤러 진입을 차단합니다.
        }

        // 3. 관리자 권한이 확인되면 요청을 그대로 진행합니다.
        return true;
    }
}