package member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import order.service.CartService; // 장바구니 서비스 임포트 추가

@Controller
public class LoginController {

	// 💡 대철이의 장바구니 비회원 연동 로직을 위해 CartService를 안전하게 주입받습니다.
	@Autowired
	private CartService cartService;

	// 1. 로그인 화면 폼 (GET)
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(Model model) {
        model.addAttribute("contentPage", "/WEB-INF/views/member/login.jsp");
        return "layout/layout";
    }

	// 2. 로그아웃 처리 (GET)
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/book";
	}

	@RequestMapping("/test-login")
	public String checkLogin(HttpServletRequest request, HttpServletResponse response) {
		// 스프링 시큐리티 컨텍스트 관제탑에서 현재 로그인 성공한 인증 정보를 꺼냅니다.
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// 만약 인증이 안 되었거나 세션이 비어있다면 로그인 폼으로 튕겨냅니다.
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return "redirect:/login?error=true";
		}

		// 시큐리티가 보증하는 로그인한 회원의 진짜 아이디를 꺼냅니다!
		String loggedInMemberId = auth.getName();

		return "redirect:/book";
	}

	// 3. 시큐리티 세션 로그인 디버깅용 텍스트 출력 메서드
	@GetMapping("/test-login-info")
	@ResponseBody
	public String checkLoginDebug() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return "로그인 안됨";
		}
		return "현재 시큐리티 로그인 사용자: " + auth.getName();
	}
}