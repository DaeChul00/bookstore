package member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        return "layout/layout"; // 전체 레이아웃 리턴
    }

    // 2. 회원가입 처리 로직
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(MemberVO vo, RedirectAttributes ra) {
        try {
            memberService.signup(vo);
            // 가입 성공 시
            return "redirect:/login";
        } catch (RuntimeException e) {
            if ("DUPLICATE_ID".equals(e.getMessage())) {
                // 중복 아이디 에러 발생 시 메시지 전달
                ra.addFlashAttribute("msg", "이미 존재하는 아이디입니다.");
                return "redirect:/signup";
            }
            return "redirect:/signup?error=fail";
        }
    }
}