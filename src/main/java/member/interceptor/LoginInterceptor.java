package member.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import member.model.MemberVO;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

        // 로그인이 안 되어 있다면
        if (loginUser == null) {
            // 로그인 페이지로 보내면서 메시지 전달
            response.sendRedirect(request.getContextPath() + "/book/list");
            return false; // 컨트롤러 진입 차단
        }

        return true; // 로그인 되어 있으면 통과
    }
}