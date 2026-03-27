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
    private MemberService memberService; // ���� ���� ȣ��

    // �α��� �� �����ֱ�
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(Model model) {
        // ���̾ƿ��� <jsp:include page="${contentPage}"/> �κп� �� ���
        model.addAttribute("contentPage", "/WEB-INF/views/member/login.jsp");
        return "layout/layout"; // ��ü ���̾ƿ� ����
    }

    // �α��� ���� ó��
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String memberId, String password, HttpSession session) {
        // ����ڰ� �Է��� ������ ȸ������ Ȯ��
        MemberVO vo = memberService.login(memberId, password);
        
        if (vo != null) {
            // �α��� ���� �� ���ǿ� ���� ������ ��°�� ���� (�������� ���� ������ ������)
            session.setAttribute("loginUser", vo);
            return "redirect:/book"; // ���� �������� �̵�
        } else {
            // �α��� ���� �� �ٽ� �α��� �������� ������ ���� �Ķ���� ����
            return "redirect:/login?error=true";
        }
    }

    // �α׾ƿ� ó��
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // ���� ���ǿ� ����� ��� ����(�α��� ���� ��)�� ����
        return "redirect:/book";
    }
}