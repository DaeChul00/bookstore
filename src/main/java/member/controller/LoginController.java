package member.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import member.model.MemberVO;
import member.service.MemberService;

@Controller
public class LoginController {
    
    @Autowired
    private MemberService memberService; // 서비스 계층 호출

    // 로그인 폼 보여주기
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String loginForm() {
        return "member/login"; // views/member/login.jsp 파일로 이동
    }

    // 로그인 실제 처리
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String memberId, String password, HttpSession session) {
        // 사용자가 입력한 값으로 회원인지 확인
        MemberVO vo = memberService.login(memberId, password);
        
        if (vo != null) {
            // 로그인 성공 시 세션에 유저 정보를 통째로 저장 (브라우저를 닫을 때까지 유지됨)
            session.setAttribute("loginUser", vo);
            return "redirect:/book/list"; // 메인 페이지로 이동
        } else {
            // 로그인 실패 시 다시 로그인 페이지로 보내며 에러 파라미터 전달
            return "redirect:/login?error=true";
        }
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 현재 세션에 저장된 모든 정보(로그인 유저 등)를 삭제
        return "redirect:/book";
    }
}