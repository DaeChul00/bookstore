package member.admin.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.model.BookVO;
import book.service.BookService;
import member.model.MemberVO;
import member.service.MemberService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    MemberService memberService; // ȸ�� ���� ����

    @RequestMapping("/memberList")
    public String memberList(
            @RequestParam(value="sort", required=false, defaultValue="new") String sort, 
            Model model, HttpSession session) {
    	
        
        // 1. ���񽺿� ���� ����(sort)�� �����Ͽ� ����Ʈ�� �����ɴϴ�.
        List<MemberVO> userList = memberService.getAllMembers(sort);
        
        // 2. ȭ�鿡 ����Ʈ�� ���� ���� ������ �ٽ� �����ϴ�.
        model.addAttribute("userList", userList);
        model.addAttribute("currentSort", sort); 
        
        model.addAttribute("contentPage", "/WEB-INF/views/admin/memberList.jsp");
        return "layout/layout";
    }
    
    @RequestMapping("/changeRole")
    public String changeRole(@RequestParam("memberId") String memberId, 
                             @RequestParam("role") String role, 
                             HttpSession session) {
        // ����: ������ üũ
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book";
        }

        // ���� ���� ���� (USER -> ADMIN / ADMIN -> USER)
        String newRole = "ADMIN".equals(role) ? "USER" : "ADMIN";
        memberService.changeRole(memberId, newRole);

        return "redirect:/admin/memberList";
    }
    
    @Autowired
    private BookService bookService; // BookService ���� �ʿ�

    @RequestMapping("/book/list")
    public String adminBookList(Model model, HttpSession session) {
        // ���ͼ��Ͱ� ������ ��Ʈ�ѷ������� �� �� �� üũ�ϸ� �����մϴ�.
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book";
        }

        // ��� ���� ���� ��������
        List<BookVO> bookList = bookService.getBooks();
        model.addAttribute("bookList", bookList);
        
        // �����ڿ� ���� ��� JSP (���� ����ž� �մϴ�)
        model.addAttribute("contentPage", "/WEB-INF/views/admin/adminBookList.jsp");
        return "layout/layout";
    }
    @RequestMapping("/deleteMember")
    public String deleteMember(@RequestParam("memberId") String memberId, HttpSession session, RedirectAttributes ra) {
        // 1. ���� üũ: �����ڸ� ���� ����
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book";
        }

        // 2. ���� ���� ���� ���� (���� ����)
        if (loginUser.getMemberId().equals(memberId)) {
            ra.addFlashAttribute("msg", "������ ���� ������ ��Ͽ��� ������ �� �����ϴ�.");
            return "redirect:/admin/memberList";
        }

        // 3. ȸ�� ���� ����
        memberService.withdraw(memberId);
        
        ra.addFlashAttribute("msg", memberId + " ������ �����Ǿ����ϴ�.");
        return "redirect:/admin/memberList";
    }
}