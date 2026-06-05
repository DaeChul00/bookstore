package member.admin.controller;

import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // 1. 회원 목록 조회
    @RequestMapping("/memberList")
    public String memberList(@RequestParam(value="sort", required=false, defaultValue="new") String sort, 
                             Model model) {

        model.addAttribute("userList", memberService.getAllMembers(sort));
        model.addAttribute("currentSort", sort); 
        model.addAttribute("contentPage", "/WEB-INF/views/admin/memberList.jsp");

        return "layout/layout";
    }

    // 2. 회원 권한 변경 (USER <-> ADMIN)
    @RequestMapping("/changeRole")
    public String changeRole(@RequestParam("memberId") String memberId, @RequestParam("role") String role) {

        String newRole = "ADMIN".equals(role) ? "USER" : "ADMIN";
        memberService.changeRole(memberId, newRole);
        return "redirect:/admin/memberList";
    }

    // 3. 관리자용 도서 목록 관리
    @RequestMapping("/book/list")
    public String adminBookList(Model model) {
        

        model.addAttribute("bookList", bookService.getBooks());
        model.addAttribute("contentPage", "/WEB-INF/views/admin/adminBookList.jsp");
        return "layout/layout";
    }

    // 4. 관리자용 회원 강제 탈퇴(삭제)
    @RequestMapping("/deleteMember")
    public String deleteMember(@RequestParam("memberId") String memberId, RedirectAttributes ra) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentAdminId = auth.getName(); 

        if (currentAdminId.equals(memberId)) {
            ra.addFlashAttribute("msg", "로그인 중인 관리자 본인 계정은 삭제할 수 없습니다.");
            return "redirect:/admin/memberList";
        }
       
        memberService.withdraw(memberId);
        
        ra.addFlashAttribute("msg", memberId + " 계정이 성공적으로 강제 탈퇴 처리되었습니다.");
        return "redirect:/admin/memberList";
    }
}