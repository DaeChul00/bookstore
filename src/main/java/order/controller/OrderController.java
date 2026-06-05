package order.controller;

import java.util.ArrayList;
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
	private OrderService orderService;
	
	// 스프링 시큐리티 컨텍스트에서 로그인된 진짜 유저 ID 추출
	private String getLoginId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return null;
		}
		return auth.getName();
	}

	// 1. 장바구니 목록 보기
	@RequestMapping("/cart")
	public String cartList(Model model) {
		String mid = getLoginId();
		if (mid == null) return "redirect:/login";

		model.addAttribute("cartList", cartService.getCartList(mid));
		model.addAttribute("contentPage", "/WEB-INF/views/order/cart.jsp");
		return "layout/layout";
	}

	// 2. 장바구니에 상품 추가 (POST 폼 매핑 완벽 대응)
	@RequestMapping("/addCart")
	public String addCart(@RequestParam("bookId") int bookId, 
	                      @RequestParam("count") int count, 
	                      RedirectAttributes ra) {
		
		String mid = getLoginId();
		
		System.out.println("====== 😎 [대철 장바구니 진짜 최종 가동] ======");
		System.out.println("▶ 현재 로그인 유저 ID: " + mid);
		System.out.println("▶ 브라우저가 던진 책 번호: " + bookId);
		System.out.println("▶ 브라우저가 던진 수량: " + count);

		if (mid == null) {
			System.out.println("❌ [인증 오류] 로그인 정보가 없어서 로그인 폼으로 튕깁니다.");
			return "redirect:/login";
		}

		CartVO cart = CartVO.builder()
		                    .memberId(mid)
		                    .bookId(bookId)
		                    .count(count)
		                    .build();

		boolean isSuccess = cartService.addCart(cart);
		System.out.println("▶ 서비스 레이어 처리 결과 사인: " + isSuccess);

		if (isSuccess) {
			ra.addFlashAttribute("msg", "장바구니에 담겼습니다.");
			System.out.println("⭕ [성공] H2 데이터베이스 반영 완료!");
		} else {
			System.out.println("❌ [실패] 서비스 연산 도중 실패 사인이 떨어졌습니다.");
		}
		System.out.println("====== 😎 [대철 장바구니 진짜 최종 종료] ======");
		
		return "redirect:/order/cart";
	}

	// 3. 장바구니 수량 변경
	@RequestMapping("/updateCart")
	public String updateCart(@RequestParam("bookId") int bookId, @RequestParam("count") int count) {
		String mid = getLoginId();
		if (mid == null) return "redirect:/login";
		cartService.updateCartCount(mid, bookId, count);
		return "redirect:/order/cart";
	}

	// 4. 장바구니 항목 개별 삭제
	@RequestMapping("/deleteCart")
	public String deleteCart(@RequestParam("bookId") int bookId) {
		String mid = getLoginId();
		if (mid == null) return "redirect:/login";
		cartService.deleteCart(mid, bookId);
		return "redirect:/order/cart";
	}

	// 5. 주문하기 (구매 프로세스 가동)
	@RequestMapping("/buy")
	public String buy() {
		String mid = getLoginId();
		if (mid == null) return "redirect:/login";

		List<CartVO> cartList = cartService.getCartList(mid);
		
		if (cartList != null && !cartList.isEmpty()) {
			List<OrderVO> orderList = new ArrayList<>();
			for (CartVO cart : cartList) {
				OrderVO order = OrderVO.builder()
						.memberId(mid)
						.bookId(cart.getBookId())
						.title(cart.getTitle())
						.count(cart.getCount())
						.orderPrice(cart.getPrice() * cart.getCount())
						.bookimage(cart.getBookimage())
						.build();
				orderList.add(order);
			}
			if (orderService.placeOrder(mid, orderList)) {
				return "redirect:/order/list";
			}
		}
		return "redirect:/order/cart";
	}

	// 6. 주문 내역 보기
	@RequestMapping("/list")
	public String orderList(Model model) {
		String mid = getLoginId();
		if (mid == null) return "redirect:/login";

		model.addAttribute("orderList", orderService.getOrderList(mid));
		model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
		return "layout/layout";
	}
}