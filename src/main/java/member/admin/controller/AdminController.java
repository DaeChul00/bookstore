package member.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
	
import book.service.BookService;
import member.service.MemberService;
import order.repository.OrderDAOH2;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private OrderDAOH2 orderDAOH2;

    // 회원 목록 조회 (시큐리티 자동 보호)
    @RequestMapping("/memberList")
    public String memberList(@RequestParam(value="sort", required=false, defaultValue="new") String sort, Model model) {
        model.addAttribute("userList", memberService.getAllMembers(sort));
        model.addAttribute("currentSort", sort); 
        model.addAttribute("contentPage", "/WEB-INF/views/admin/memberList.jsp");
        return "layout/layout";
    }

    // 회원 권한 변경
    @RequestMapping("/changeRole")
    public String changeRole(@RequestParam("memberId") String memberId, @RequestParam("role") String role) {
        String newRole = "ADMIN".equals(role) ? "USER" : "ADMIN";
        memberService.changeRole(memberId, newRole);
        return "redirect:/admin/memberList";
    }

    // 관리자용 도서 목록 관리
    @RequestMapping("/book/list")
    public String adminBookList(Model model) {
        model.addAttribute("bookList", bookService.getBooks());
        model.addAttribute("contentPage", "/WEB-INF/views/admin/adminBookList.jsp");
        return "layout/layout";
    }
    
    // 회원 강제 탈퇴
    @RequestMapping("/deleteMember")
    public String deleteMember(@RequestParam("memberId") String memberId, RedirectAttributes ra) {
        memberService.withdraw(memberId);
        ra.addFlashAttribute("msg", memberId + " 계정이 삭제되었습니다.");
        return "redirect:/admin/memberList";
    }
    
    // 관리자 전용 전체 회원 주문/배송 목록 조회

    @RequestMapping("/order/list")
    public String adminOrderList(Model model) {
        model.addAttribute("adminOrderList", orderDAOH2.findAllOrdersForAdmin());
        model.addAttribute("contentPage", "/WEB-INF/views/admin/adminOrderList.jsp");
        return "layout/layout";
    }

    // 관리자용 배송 상태 변경 처리 (시큐리티 호환을 위해 POST 권장)
    @RequestMapping(value = "/order/updateStatus", method = RequestMethod.POST)
    public String updateDeliveryStatus(@RequestParam int orderId, @RequestParam String deliveryStatus) {
        orderDAOH2.updateDeliveryStatus(orderId, deliveryStatus);
        return "redirect:/admin/order/list"; 
    }
}