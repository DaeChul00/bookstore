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

    // 6. 주문 내역 보기 (order-list.jsp 연결)
    @RequestMapping("/list")
    public String orderList(HttpSession session, Model model) {
        String mid = getLoginId(session);
        if (mid == null) return "redirect:/login";

        model.addAttribute("orderList", orderService.getOrderList(mid));
        model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
        return "layout/layout";
    }
    
    @RequestMapping(value = "/review-insert", method = RequestMethod.POST)
    @ResponseBody // HTML 템플릿 페이지 뷰가 아닌 순수 데이터 응답용 문자열 스트림 처리
    public String insertReview(
            @RequestParam("bookId") int bookId,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            HttpSession session) {
        
        // 서버 콘솔을 통해 파라미터가 유실 없이 도달했는지 육안으로 최종 검증합니다.
        System.out.println(">>> [리뷰등록 API] 넘어온 bookId  : " + bookId);
        System.out.println(">>> [리뷰등록 API] 넘어온 rating  : " + rating);
        System.out.println(">>> [리뷰등록 API] 넘어온 content : " + content);
        
        // 세션 유효성 검증 체킹
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "login_required";
        }
        
        // 서비스 단을 호출하여 가용한 DB 레포지토리에 INSERT 쿼리를 실행하도록 명령을 위임합니다.
        boolean isSuccess = reviewService.addReview(bookId, loginUser.getMemberId(), rating, content);
        
        return isSuccess ? "success" : "fail";
    }
    
    // 세션에서 아이디 가져오는 공통 메서드
    private String getMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        return (memberId != null) ? memberId : "user01";
    }
}