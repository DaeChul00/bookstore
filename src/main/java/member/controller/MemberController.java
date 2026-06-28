package member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        // 1. DB 주소 테이블 및 회원 테이블 데이터 정상 반영
        memberService.updateMember(vo);
        
        // ⭕ 2. [핵심 조치] 스프링 시큐리티 세션 정보(Authentication) 실시간 강제 갱신
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // 기존 시큐리티 인증 객체에서 권한 리스트(Authorities)를 유지한 채, 
            // 방금 주소(ZIPCODE, ROAD_ADDRESS)가 업데이트된 vo 객체 또는 Principal로 세션을 새로 교체합니다.
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                vo, // 세션 Principal에 새 데이터를 밀어 넣음 (또는 auth.getPrincipal() 구조에 맞춰 적용)
                auth.getCredentials(), 
                auth.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        
        ra.addFlashAttribute("msg", "회원 정보가 성공적으로 수정되었습니다! ✨");
        
        // 3. 내 정보 수정 폼으로 리다이렉트 (이제 세션이 갱신되어 빈칸으로 초기화되지 않고 유지됨)
        return "redirect:/member/update"; 
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