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
    MemberService memberService;
    @Autowired
    private BookService bookService;

    // 회원 목록 조회
    @RequestMapping("/memberList")
    public String memberList(@RequestParam(value="sort", required=false, defaultValue="new") String sort, 
                             Model model, HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book/list";
        }

        model.addAttribute("userList", memberService.getAllMembers(sort));
        model.addAttribute("currentSort", sort); 
        model.addAttribute("contentPage", "/WEB-INF/views/admin/memberList.jsp");

        return "layout/layout";
    }

    // 회원 권한 변경 (USER <-> ADMIN)
    @RequestMapping("/changeRole")
    public String changeRole(@RequestParam("memberId") String memberId, @RequestParam("role") String role, HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) 
        	return "redirect:/book/list";

        String newRole = "ADMIN".equals(role) ? "USER" : "ADMIN";
        memberService.changeRole(memberId, newRole);
        return "redirect:/admin/memberList";
    }

    // 관리자용 도서 목록 관리
    @RequestMapping("/book/list")
    public String adminBookList(Model model, HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) return "redirect:/book/list";

        model.addAttribute("bookList", bookService.getBooks());
        model.addAttribute("contentPage", "/WEB-INF/views/admin/adminBookList.jsp");
        return "layout/layout";
    }
    @RequestMapping("/deleteMember")
    public String deleteMember(@RequestParam("memberId") String memberId, HttpSession session, RedirectAttributes ra) {
        
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book";
        }

        if (loginUser.getMemberId().equals(memberId)) {
            ra.addFlashAttribute("msg", "                     Ͽ                   ϴ .");
            return "redirect:/admin/memberList";
        }
       
        memberService.withdraw(memberId);
        
        ra.addFlashAttribute("msg", memberId + "             Ǿ    ϴ .");
        return "redirect:/admin/memberList";
    }
}