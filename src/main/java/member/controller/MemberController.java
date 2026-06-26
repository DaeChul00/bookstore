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

import email.service.EmailService;
import member.model.MemberVO;
import member.service.MemberService;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private EmailService emailService;

    // 1. 회원가입 화면 출력
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupForm(Model model) {
        model.addAttribute("contentPage", "/WEB-INF/views/member/signup.jsp");
        return "layout/layout";
    }

    // 2. 아이디 중복 + 새 이메일 가드 통합 회원가입 처리
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(MemberVO vo, RedirectAttributes ra) {
        
        // 이메일 중복 체크 선행
        MemberVO existingEmail = memberService.findByEmail(vo.getEmail());
        if (existingEmail != null) {
            ra.addFlashAttribute("msg", "이미 가입된 이메일입니다.");
            return "redirect:/signup";
        }

        try {
            memberService.signup(vo);
            ra.addFlashAttribute("msg", "회원가입 성공");
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("이미 존재하는 아이디")) {
                ra.addFlashAttribute("msg", "이미 존재하는 아이디입니다.");
                return "redirect:/signup";
            }
            
            ra.addFlashAttribute("msg", "회원가입 실패");
            return "redirect:/signup";
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("msg", "회원가입 실패 (서버 오류)");
            return "redirect:/signup";
        }
    }
    // 3. 회원 정보 수정 폼 띄우기 (시큐리티 인증 정보 기반 안전 처리 유지)
    @RequestMapping(value = "/member/update", method = RequestMethod.GET)
    public String updateForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }

        String loggedInId = auth.getName();
        MemberVO member = memberService.login(loggedInId, null); 
            
        model.addAttribute("loginUser", member);
        model.addAttribute("contentPage", "/WEB-INF/views/member/MemberUpdateForm.jsp");
        return "layout/layout";
    }

    // 4. 회원 정보 수정 처리
    @RequestMapping(value = "/member/update", method = RequestMethod.POST)
    public String update(MemberVO vo, HttpSession session, RedirectAttributes ra) {
        memberService.updateMember(vo);
        ra.addFlashAttribute("msg", "회원 정보가 수정되었습니다. 다시 로그인해 주세요.");
        session.invalidate();
        return "redirect:/login";
    }

    // 5. 회원 탈퇴
    @RequestMapping("/member/withdraw")
    public String withdraw(HttpSession session, RedirectAttributes ra) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            memberService.withdraw(auth.getName());
            session.invalidate();
            SecurityContextHolder.clearContext();
            ra.addFlashAttribute("msg", "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.");
        }
        return "redirect:/book";
    }
}