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
    MemberService memberService; // 회원 서비스 주입

    @RequestMapping("/memberList")
    public String memberList(
            @RequestParam(value="sort", required=false, defaultValue="new") String sort, 
            Model model, HttpSession session) {
        
        // 1. 서비스에 정렬 기준(sort)을 전달하여 리스트를 가져옵니다.
        List<MemberVO> userList = memberService.getAllMembers(sort);
        
        // 2. 화면에 리스트와 현재 정렬 기준을 다시 보냅니다.
        model.addAttribute("userList", userList);
        model.addAttribute("currentSort", sort); 
        
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
    @RequestMapping("/deleteMember")
    public String deleteMember(@RequestParam("memberId") String memberId, HttpSession session, RedirectAttributes ra) {
        // 1. 보안 체크: 관리자만 접근 가능
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/book/list";
        }

        // 2. 본인 계정 삭제 방지 (선택 사항)
        if (loginUser.getMemberId().equals(memberId)) {
            ra.addFlashAttribute("msg", "관리자 본인 계정은 목록에서 삭제할 수 없습니다.");
            return "redirect:/admin/memberList";
        }

        // 3. 회원 삭제 실행
        memberService.withdraw(memberId);
        
        ra.addFlashAttribute("msg", memberId + " 계정이 삭제되었습니다.");
        return "redirect:/admin/memberList";
    }
}