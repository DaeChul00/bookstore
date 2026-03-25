package member.admin.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import book.model.BookVO;
import book.service.BookService;
import member.model.MemberVO;
import member.service.MemberService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    MemberService memberService; // 회원 서비스 주입

    @RequestMapping("/memberList")
    public String memberList(Model model, HttpSession session) {
        // 관리자 권한 확인
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book/list";
        }

        // 전체 회원 목록 가져오기
        List<MemberVO> userList = memberService.getAllMembers();
        model.addAttribute("userList", userList);
        
        // 레이아웃 적용
        model.addAttribute("contentPage", "/WEB-INF/views/admin/memberList.jsp");
        return "layout/layout";
    }
    
    @RequestMapping("/changeRole")
    public String changeRole(@RequestParam("memberId") String memberId, 
                             @RequestParam("role") String role, 
                             HttpSession session) {
        // 보안: 관리자 체크
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book/list";
        }

        // 권한 반전 로직 (USER -> ADMIN / ADMIN -> USER)
        String newRole = "ADMIN".equals(role) ? "USER" : "ADMIN";
        memberService.changeRole(memberId, newRole);

        return "redirect:/admin/memberList";
    }
    
    @Autowired
    private BookService bookService; // BookService 주입 필요

    @RequestMapping("/book/list")
    public String adminBookList(Model model, HttpSession session) {
        // 인터셉터가 있지만 컨트롤러에서도 한 번 더 체크하면 안전합니다.
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book/list";
        }

        // 모든 도서 정보 가져오기
        List<BookVO> bookList = bookService.getBooks();
        model.addAttribute("bookList", bookList);
        
        // 관리자용 도서 목록 JSP (새로 만드셔야 합니다)
        model.addAttribute("contentPage", "/WEB-INF/views/admin/adminBookList.jsp");
        return "layout/layout";
    }
}