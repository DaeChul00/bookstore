package order.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import order.model.CartVO;
import order.model.OrderVO;
import order.service.CartService;
import order.service.OrderService;
import book.model.BookVO;
import book.service.BookService;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private BookService bookService;

	// 시큐리티 금고에서 유저 ID 안전하게 추출하도록 정돈
	private String getLoginId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return null;
		}
		return auth.getName();
	}

	// 1. 장바구니 목록 보기 (비회원 쿠키 / 회원 DB 분기 일원화)
	@RequestMapping("/cart")
	public String cartList(HttpServletRequest request, Model model) {
		String mid = getLoginId();

		if (mid == null) {
			List<CartVO> guestCartList = new ArrayList<>();
			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("guestCart".equals(cookie.getName())) {
						try {
							String cartJson = URLDecoder.decode(cookie.getValue(), "UTF-8");
							String cleanJson = cartJson.replace("[", "").replace("]", "").replace("{", "").replace("}",
									"");

							if (!cleanJson.trim().isEmpty()) {
								String[] items = cleanJson.split(",(?=\\\"bookId\\\")");
								for (String item : items) {
									String[] parts = item.split(",");
									int parsedBookId = 0;
									int parsedCount = 0;

									for (String part : parts) {
										if (part.contains("bookId")) {
											parsedBookId = Integer.parseInt(part.split(":")[1].trim());
										} else if (part.contains("count")) {
											parsedCount = Integer.parseInt(part.split(":")[1].trim());
										}
									}

									if (parsedBookId > 0 && parsedCount > 0) {
										BookVO book = bookService.getBook(parsedBookId);
										if (book != null) {
											CartVO vo = CartVO.builder().cartId(0).bookId(parsedBookId)
													.count(parsedCount).title(book.getTitle()).price(book.getPrice())
													.bookimage(book.getBookimage()).build();
											guestCartList.add(vo);
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			model.addAttribute("cartList", guestCartList);
		} else {
			model.addAttribute("cartList", cartService.getCartList(mid));
		}

		model.addAttribute("contentPage", "/WEB-INF/views/order/cart.jsp");
		return "layout/layout";
	}

	// 2. 비동기 수량 변경 매핑 (수동 파라미터 매핑 조율)
	@PostMapping("/updateCartAsync")
	@ResponseBody
	public Map<String, String> updateCartAsync(@RequestParam("cartId") int cartId, @RequestParam("count") int count) {
		Map<String, String> result = new HashMap<>();
		boolean success = cartService.updateCartCount(getLoginId(), cartId, count);
		result.put("status", success ? "success" : "fail");
		return result;
	}

	// 3. 회원용 장바구니 상품 추가
	@RequestMapping("/addCart")
	public String addCart(@RequestParam("bookId") int bookId, @RequestParam("count") int count, RedirectAttributes ra) {

		String mid = getLoginId();

		if (mid == null) {
			System.out.println("ℹ️ 비회원 장바구니 추가 요청 감지: 쿠키 보존 상태 유지 후 목록 이동");
			ra.addFlashAttribute("msg", "장바구니에 상품이 담겼습니다.");
			return "redirect:/order/cart";
		}

// 회원일 때만 DB 적재 파이프라인 가동
		CartVO cart = CartVO.builder().memberId(mid).bookId(bookId).count(count).build();

		boolean isSuccess = cartService.addCart(cart);
		if (isSuccess) {
			ra.addFlashAttribute("msg", "장바구니에 담겼습니다.");
		}

		return "redirect:/order/cart";
	}

	// 4. 회원용 장바구니 항목 개별 삭제
	@RequestMapping("/deleteCart")
	public String deleteCart(@RequestParam("cartId") int bookId) {
		cartService.deleteCart(getLoginId(), bookId);
		return "redirect:/order/cart";
	}

	// 5. 주문 프로세스
	@RequestMapping("/buy")
	public String buy() {
		String mid = getLoginId();
		if (mid == null)
			return "redirect:/login";
		List<CartVO> cartList = cartService.getCartList(mid);

		if (cartList != null && !cartList.isEmpty()) {
			// placeOrder 최신 서비스 스펙 동기화
			List<OrderVO> orderList = new ArrayList<>();
			for (CartVO cart : cartList) {
				orderList.add(OrderVO.builder().memberId(mid).bookId(cart.getBookId()).title(cart.getTitle())
						.count(cart.getCount()).orderPrice(cart.getPrice() * cart.getCount())
						.bookimage(cart.getBookimage()).build());
			}
			orderService.placeOrder(mid, orderList);
			return "redirect:/order/list";
		}
		return "redirect:/order/cart";
	}

	// 6. 주문 내역 보기
	@RequestMapping("/list")
	public String orderList(Model model) {
		String mid = getLoginId();
		if (mid == null)
			return "redirect:/login";

		model.addAttribute("orderList", orderService.getOrderList(mid));
		model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
		return "layout/layout";
	}
}