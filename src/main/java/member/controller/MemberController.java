package member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import member.model.MemberVO;
import member.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	private MemberService memberService;

	// 1. 회원가입 화면 띄우기 (URL: /signup)
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupForm(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/signup.jsp");
		return "layout/layout";
	}

	// 2. 회원가입 처리 로직
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(MemberVO vo, RedirectAttributes ra) {
		try {
			memberService.signup(vo);
			return "redirect:/login";
		} catch (RuntimeException e) {
			
			e.printStackTrace();
			
			if ("DUPLICATE_ID".equals(e.getMessage())) {
				ra.addFlashAttribute("msg", "이미 존재하는 아이디입니다.");
				return "redirect:/signup";
			}
			return "redirect:/signup?error=fail";
		}
	}

	// 3. 회원 정보 수정 폼 띄우기 (URL: /member/update)
	@RequestMapping(value = "/member/update", method = RequestMethod.GET)
	public String updateForm(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return "redirect:/login";
		}

			// 로그인한 사용자의 ID로 DB에서 회원 정보를 가져와 모델에 심어줍니다.
		String loggedInId = auth.getName();
		member.model.MemberVO member = memberService.login(loggedInId, null); 
			
		model.addAttribute("loginUser", member);
		model.addAttribute("contentPage", "/WEB-INF/views/member/MemberUpdateForm.jsp");
		return "layout/layout";
	}

	// 4. 회원 정보 수정 처리 (수정 후 세션 무효화 처리를 통해 시큐리티 정보 갱신 유도)
	@RequestMapping(value = "/member/update", method = RequestMethod.POST)
	public String update(MemberVO vo, HttpSession session, RedirectAttributes ra) {
		// 1. DB 정보를 수정합니다.
		memberService.updateMember(vo);

		// 2. 알림 메시지 전달
		ra.addFlashAttribute("msg", "회원 정보가 수정되었습니다. 다시 로그인해 주세요.");
		
		session.invalidate();
		
		return "redirect:/login"; 
	}
	
	// 5. 회원 탈퇴 (시큐리티 인증 객체 기반 완전 초기화)
	@RequestMapping("/member/withdraw")
	public String withdraw(HttpSession session, RedirectAttributes ra) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			// 1. 시큐리티 인증 객체에서 아이디를 꺼내와 DB에서 탈퇴 처리합니다.
			memberService.withdraw(auth.getName());
			
			// 2. 세션 완전히 무효화 (로그아웃 처리)
			session.invalidate();
			
			// 3. 시큐리티 관제탑 컨텍스트도 깨끗하게 클리어해 줍니다.
			SecurityContextHolder.clearContext();
			
			ra.addFlashAttribute("msg", "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.");
		}
		
		return "redirect:/book";
	}
}