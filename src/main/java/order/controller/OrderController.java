package order.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import order.model.CartVO;
import order.model.OrderVO;
import order.service.CartService;
import order.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService; // 주문 처리를 위한 서비스 (새로 추가)

    // 1. 장바구니 목록 보기 (cart.jsp 연결)
    @RequestMapping("/cart")
    public String cartList(HttpSession session, Model model) {
        String memberId = getMemberId(session);
        List<CartVO> list = cartService.getCartList(memberId);
        
        model.addAttribute("cartList", list);
        model.addAttribute("contentPage", "/WEB-INF/views/order/cart.jsp");
        return "layout/layout";
    }

    // 2. 장바구니에 상품 추가 (상세페이지 등에서 호출)
    @RequestMapping("/addCart")
    public String addCart(CartVO cart, HttpSession session, RedirectAttributes ra) {
        cart.setMemberId(getMemberId(session));
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
        String memberId = getMemberId(session);
        List<CartVO> cartList = cartService.getCartList(memberId);

        if (cartList != null && !cartList.isEmpty()) {
            // 장바구니 데이터를 주문 데이터로 변환하여 저장하고 장바구니 비우기
            orderService.processOrder(memberId, cartList);
            return "redirect:/order/list";
        }
        return "redirect:/order/cart";
    }

    // 6. 주문 내역 보기 (order-list.jsp 연결)
    @RequestMapping("/list")
    public String orderList(HttpSession session, Model model) {
        String memberId = getMemberId(session);
        List<OrderVO> list = orderService.getOrderList(memberId);
        
        model.addAttribute("orderList", list);
        model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
        return "layout/layout";
    }

    // 세션에서 아이디 가져오는 공통 메서드 (중복 코드 방지)
    private String getMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        return (memberId != null) ? memberId : "user01"; // 테스트용 임시 아이디
    }
}