package member.controller;

import javax.servlet.http.HttpSession;

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
 // 1. 수정 폼 띄우기 (현재 로그인한 유저 정보를 담아서 보냄)
    @RequestMapping(value = "/member/update", method = RequestMethod.GET)
    public String updateForm(Model model, HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        model.addAttribute("contentPage", "/WEB-INF/views/member/MemberUpdateForm.jsp");
        return "layout/layout";
    }

    // 2. 수정 처리
    @RequestMapping(value = "/member/update", method = RequestMethod.POST)
    public String update(MemberVO vo, HttpSession session, RedirectAttributes ra) {
        // 1. DB 정보를 수정합니다.
        memberService.updateMember(vo);
        
        // 2. 세션에 저장된 기존 로그인 정보를 가져옵니다.
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        
        if (loginUser != null) {
            // 3. 세션 객체의 내용을 사용자가 새로 입력한 값으로 덮어씌웁니다.
            loginUser.setName(vo.getName());
            loginUser.setEmail(vo.getEmail());
            
            // 4. 변경된 객체를 세션에 다시 저장 (갱신)
            session.setAttribute("loginUser", loginUser);
        }

        // 알림 메시지 전달 (선택 사항)
        ra.addFlashAttribute("msg", "정보가 수정되었습니다.");
        
        // 로그인이 풀리지 않은 상태로 리스트로 이동합니다.
        return "redirect:/book/list"; 
    }
    
    @RequestMapping("/member/withdraw")
    public String withdraw(HttpSession session, RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        
        if (loginUser != null) {
            // 1. DB에서 삭제
            memberService.withdraw(loginUser.getMemberId());
            
            // 2. 세션 무효화 (로그아웃)
            session.invalidate();
            
            ra.addFlashAttribute("msg", "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.");
        }
        
        return "redirect:/book/list";
    }
}