package member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    // 로그인 화면
    @GetMapping("/login")
    public String loginForm(Model model) {

        model.addAttribute(
                "contentPage",
                "/WEB-INF/views/member/login.jsp");

        return "layout/layout";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/book";
    }
    
    
    @GetMapping("/test-login")
    @ResponseBody // HTML 화면 대신 리턴 문자열을 그대로 브라우저에 출력함
    public String checkLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return "로그인 안됨";
        }

        return "로그인 사용자: " + auth.getName();
    }
}

//package member.controller;
//
//import javax.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import member.model.MemberVO;
//import member.service.MemberService;
//
//@Controller
//public class LoginController {
//    
//    @Autowired
//    private MemberService memberService; // ������������ ������������ ��������
//
//    // �������������� ������ ��������������������
//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public String loginForm(Model model) {
//        // ���������������������� <jsp:include page="${contentPage}"/> ���������� ��������� ���������
//        model.addAttribute("contentPage", "/WEB-INF/views/member/login.jsp");
//        return "layout/layout"; // �������� ���������������� ������������
//    }
//
//    // �������������� ������������ ��������
//    @RequestMapping(value = "/login",method = RequestMethod.POST)
//    public String login(String memberId, String password, HttpSession session) {
//        // ����������������� �������������� ������������������ �������������������� ��������
//        MemberVO vo = memberService.login(memberId, password);
//        
//        if (vo != null) {
//            // �������������� ������������ ������ �������������� ������������ ������������������ �������������� ������������ (������������������������ ������������ ������������������ ������������������)
//            session.setAttribute("loginUser", vo);
//            return "redirect:/book"; // ������������ ������������������������ ��������
//        } else {
//            // �������������� ������������ ������ �������� �������������� ������������������������ ������������������ ������������ ����������������� ������������
//            return "redirect:/login?error=true";
//        }
//    }
//
//    // ������������ ��������
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate(); // ������������ �������������� ��������������� ��������� ������������(�������������� ������������ ������)������ ������������
//        return "redirect:/book";
//    }
//}