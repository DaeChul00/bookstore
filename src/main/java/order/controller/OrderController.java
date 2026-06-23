package order.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.service.BookService;
import order.model.CartVO;
import order.model.OrderVO;
import order.service.CartService;
import order.service.OrderService;
import order.service.ReviewService;
import member.model.MemberVO;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private BookService bookService;
    
	@Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ReviewService reviewService;
    
    private String getLoginId(HttpSession session) {
        MemberVO user = (MemberVO) session.getAttribute("loginUser");
        return (user != null) ? user.getMemberId() : null;
    }

    // 1. 장바구니 목록 보기 (cart.jsp 연결)
    @RequestMapping("/cart")
    public String cartList(HttpSession session, Model model) {
        String mid = getLoginId(session);
        if (mid == null) return "redirect:/login"; // 로그인 안됐으면 튕겨냄

        model.addAttribute("cartList", cartService.getCartList(mid));
        model.addAttribute("contentPage", "/WEB-INF/views/order/cart.jsp");
        return "layout/layout";
    }

    // 2. 장바구니에 상품 추가 (상세페이지 등에서 호출)
    @RequestMapping("/addCart")
    public String addCart(CartVO cart, HttpSession session, RedirectAttributes ra) {
        String mid = getLoginId(session);
        if (mid == null) return "redirect:/login";

        cart.setMemberId(mid);
        if (cartService.addCart(cart)) {
            ra.addFlashAttribute("msg", "장바구니에 담겼습니다.");
        }
        return "redirect:/order/cart";
    }

    // 3. 장바구니 수량 변경
    @RequestMapping("/updateCart")
    public String updateCart(@RequestParam int cartId, @RequestParam int count) {
        cartService.updateCartCount(cartId, count);
        return "redirect:/order/cart";
    }

    // 4. 장바구니 항목 삭제
    @RequestMapping("/deleteCart")
    public String deleteCart(@RequestParam int cartId) {
        cartService.deleteCart(cartId);
        return "redirect:/order/cart";
    }

    // 5. 주문하기 (구매 프로세스)
    @RequestMapping("/buy")
    public String buy(HttpSession session) {
        String mid = getLoginId(session);
        List<CartVO> cartList = cartService.getCartList(mid);
        
        if (cartList != null && !cartList.isEmpty()) {
            orderService.processOrder(mid, cartList);
            return "redirect:/order/list";
        }
        return "redirect:/order/cart";
    }
    
    // 세션에서 아이디 가져오는 공통 메서드
    private String getMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        return (memberId != null) ? memberId : "user01";
    }

    // 6. 로그인 회원별 주문 내역 조회
    @RequestMapping("/list")
    public String orderList(HttpSession session, Model model) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        List<OrderVO> orderList = orderService.findOrdersByMemberId(loginUser.getMemberId());
        
        model.addAttribute("orderList", orderList);
        model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
        
        return "layout/layout";
    }
    
    //중복 리뷰 처리
    @ResponseBody
	@RequestMapping(value = "review-insert", method = RequestMethod.POST)
	public String insertReview(@RequestParam("bookId") int bookId,
	                           @RequestParam("rating") int rating,
	                           @RequestParam("content") String content,
	                           HttpSession session) {
		
		// 1. 로그인 체크
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "login_required";
		}
		
		// 2. 중복 체크 가드 로직
		boolean isDuplicate = bookService.hasAlreadyReviewed(bookId, loginUser.getMemberId());
		if (isDuplicate) {
			return "already_exists";
		}
		
		// 3. 서비스 호출 (bookId, memberId, rating, content 순서 일치 확인)
		boolean result = reviewService.addReview(bookId, loginUser.getMemberId(), rating, content);
		
		return result ? "success" : "fail";
	}
}