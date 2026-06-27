package order.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.model.BookVO;
import book.service.BookService;
import member.model.MemberAddressVO;
import order.model.CartVO;
import order.model.OrderVO;
import order.service.CartService;
import order.service.OrderService;
import order.service.ReviewService;

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

	// 스프링 시큐리티에서 로그인한 회원의 진짜 아이디를 추출하는 유틸 메서드
	private String getSecurityLoginId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return null;
		}
		return auth.getName();
	}

	// 1. 회원/비회원 통합 장바구니 화면 출력
	@RequestMapping("/cart")
	public String cartList(HttpServletRequest request, Model model) {
		String mid = getSecurityLoginId();

		if (mid != null) {
			model.addAttribute("cartList", cartService.getCartList(mid));
		} else {
			List<CartVO> guestCartList = new ArrayList<>();
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("guestCart".equals(cookie.getName())) {
						try {
							String decodedCookie = URLDecoder.decode(cookie.getValue(), "UTF-8");
							ObjectMapper mapper = new ObjectMapper();
							List<java.util.Map<String, Object>> list = mapper.readValue(decodedCookie,
									new TypeReference<List<java.util.Map<String, Object>>>() {
									});

							for (java.util.Map<String, Object> map : list) {
								int bookId = (Integer) map.get("bookId");
								int count = (Integer) map.get("count");
								BookVO book = bookService.getBook(bookId);
								if (book != null) {
									guestCartList
											.add(CartVO.builder().bookId(bookId).count(count).title(book.getTitle())
													.price(book.getPrice()).bookimage(book.getBookimage()).build());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			model.addAttribute("cartList", guestCartList);
		}

		model.addAttribute("contentPage", "/WEB-INF/views/order/cart.jsp");
		return "layout/layout";
	}

	// 2. 장바구니 담기
	@RequestMapping("/addCart")
	public String addCart(@RequestParam("bookId") int bookId, @RequestParam("count") int count,
			HttpServletRequest request, HttpServletResponse response) {

		String mid = getSecurityLoginId();

		if (mid != null) {
			CartVO cart = CartVO.builder().memberId(mid).bookId(bookId).count(count).build();
			cartService.addCart(cart);
		} else {
			Cookie[] cookies = request.getCookies();
			String guestCartValue = "";
			List<java.util.Map<String, Object>> cartList = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();

			try {
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if ("guestCart".equals(cookie.getName())) {
							guestCartValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
							cartList = mapper.readValue(guestCartValue,
									new TypeReference<List<java.util.Map<String, Object>>>() {
									});
							break;
						}
					}
				}

				boolean isExist = false;
				for (java.util.Map<String, Object> map : cartList) {
					if ((Integer) map.get("bookId") == bookId) {
						map.put("count", (Integer) map.get("count") + count);
						isExist = true;
						break;
					}
				}

				if (!isExist) {
					java.util.Map<String, Object> newProduct = new java.util.HashMap<>();
					newProduct.put("bookId", bookId);
					newProduct.put("count", count);
					cartList.add(newProduct);
				}

				String jsonStr = mapper.writeValueAsString(cartList);
				Cookie cartCookie = new Cookie("guestCart", URLEncoder.encode(jsonStr, "UTF-8"));
				cartCookie.setPath("/");
				cartCookie.setMaxAge(30 * 24 * 60 * 60);
				response.addCookie(cartCookie);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:/order/cart";
	}

	// 3. 비동기 수량 변경 처리 (대철 수량 제어)
	@ResponseBody
	@RequestMapping(value = "/updateCartAsync", method = RequestMethod.POST)
	public String updateCartAsync(@RequestParam("bookId") int bookId, @RequestParam("count") int count) {
		String mid = getSecurityLoginId();
		if (mid == null)
			return "login_required";

		boolean ok = cartService.updateCartCount(mid, bookId, count);
		return ok ? "success" : "fail";
	}

	// 4. 장바구니 삭제
	@RequestMapping("/deleteCart")
	public String deleteCart(@RequestParam("cartId") int bookId) {
		String mid = getSecurityLoginId();
		if (mid == null)
			return "redirect:/login";

		cartService.deleteCart(mid, bookId);
		return "redirect:/order/cart";
	}

	@RequestMapping("/buy")
	public String buy(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String mid = getSecurityLoginId(); // 로그인 안 되어 있으면 null 또는 anonymousUser 반환

		// 시큐리티 익명 사용자 문자열 가드 처리
		if ("anonymousUser".equals(mid)) {
			mid = null;
		}

		List<OrderVO> orderList = new ArrayList<>();

		if (mid != null) {
			// ⭕ [회원 주문 케이스] DB 장바구니에서 리스트 가져오기
			List<CartVO> cartList = cartService.getCartList(mid);
			if (cartList != null && !cartList.isEmpty()) {
				for (CartVO cart : cartList) {
					orderList.add(OrderVO.builder().memberId(mid).bookId(cart.getBookId()).title(cart.getTitle())
							.count(cart.getCount()).orderPrice(cart.getPrice() * cart.getCount())
							.bookimage(cart.getBookimage()).deliveryStatus("주문완료").build());
				}
				orderService.placeOrder(mid, orderList); // DB 장바구니 비우기 포함된 서비스
				return "redirect:/order/list"; // 회원 주문 내역 페이지로 이동
			}
		} else {
			// ⭕ [비회원 주문 케이스] 로그인 창으로 안 보내고 브라우저 쿠키(guestCart) 파싱해서 주문 넣기
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("guestCart".equals(cookie.getName()) && cookie.getValue() != null
							&& !cookie.getValue().isEmpty()) {
						try {
							String cartJson = URLDecoder.decode(cookie.getValue(), "UTF-8");
							// 💡 쿠키 문자열을 파싱하여 가공하는 로직 수행 (시큐리티 successHandler에 두신 파싱 로직 참고)
							// ... 파싱하여 파악한 bookId, count 정보를 바탕으로 orderList에 추가 ...

							// 비회원 주문 전송 호출 (MEMBER_ID가 null로 들어감)
							// orderService.placeGuestOrder(orderList);

							// 사용 완료한 비회원 장바구니 쿠키 삭제 조치
							cookie.setValue("");
							cookie.setPath("/");
							cookie.setMaxAge(0);
							response.addCookie(cookie);

							return "redirect:/book"; // 비회원은 주문내역 페이지가 없으므로 메인으로 홈 리다이렉트
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		return "redirect:/order/cart";
	}
	
	//5.비회원 주문내역
	@RequestMapping("/nmorderlist")
	public String nonMemberOrderList(HttpSession session, Model model) {

		// 세션에서 주문 번호 꺼내기
        Integer guestOrderId = (Integer) session.getAttribute("guestOrderId");
        
        if (guestOrderId != null) {
            // 주문 내역이 있으면 DB에서 리스트를 긁어옴
            List<OrderVO> orderList = orderService.findNonMemberOrders(guestOrderId);
            model.addAttribute("orderList", orderList);
        } else {
            // 주문한 적이 없으면 빈 리스트 전달
            model.addAttribute("orderList", new ArrayList<OrderVO>());
        }
        
        // 전체 레이아웃 템플릿에 알맹이 배치
        model.addAttribute("contentPage", "/WEB-INF/views/order/nmorderlist.jsp");
        model.addAttribute("showBanner", false); 
        return "layout/layout";
	}

	// 6. 나의 주문 내역 조회
	@RequestMapping("/list")
	public String orderList(Model model) {
		String mid = getSecurityLoginId();
		if (mid == null)
			return "redirect:/login";

		List<OrderVO> orderList = orderService.findOrdersByMemberId(mid);
		model.addAttribute("orderList", orderList);
		model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
		return "layout/layout";
	}

	// 7. 한줄평 리뷰 등록 가드
	@ResponseBody
	@RequestMapping(value = "/review-insert", method = RequestMethod.POST)
	public String insertReview(@RequestParam("bookId") int bookId, @RequestParam("rating") int rating,
			@RequestParam("content") String content) {
		String mid = getSecurityLoginId();
		if (mid == null)
			return "login_required";

		boolean isDuplicate = bookService.hasAlreadyReviewed(bookId, mid);
		if (isDuplicate)
			return "already_exists";

		boolean success = reviewService.addReview(bookId, mid, rating, content);
		return success ? "success" : "fail";
	}

	// 실시간 배송 상태 변경 모니터링용 비동기 API
	@RequestMapping(value = "/status", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public String getOrderStatus(@RequestParam int orderId) {
		return orderService.getOrderStatus(orderId);
	}

	// 배송 정보 상세 페이지 포워딩 처리
	@RequestMapping("/detail")
	public String orderDetail(@RequestParam int orderId, Model model) {
		OrderVO order = orderService.getOrderById(orderId);
		model.addAttribute("order", order);
		model.addAttribute("contentPage", "/WEB-INF/views/order/order_detail.jsp");
		return "layout/layout";
	}
	
		// ✨ [통합 완료] 회원 / 비회원 주문 최종 제출 처리 관문
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public String orderSubmit(@ModelAttribute OrderVO orderVO, HttpSession session, 
	                          HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) {
		
		String loginId = getSecurityLoginId();

		if (loginId == null) {
			// ⭕ [A. 비회원 주문 케이스]
			orderVO.setMemberId(null);
			orderVO.setDeliveryStatus("결제완료");

			int generatedOrderId = orderService.nonlogInsert(orderVO);
			session.setAttribute("guestOrderId", generatedOrderId);

			// 💡 [추가] 주문이 완료되었으므로 비회원의 장바구니 쿠키(guestCart)를 완전히 비워줍니다.
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("guestCart".equals(cookie.getName())) {
						cookie.setValue("");
						cookie.setPath("/");
						cookie.setMaxAge(0); // 쿠키 수명을 0으로 만들어 삭제
						response.addCookie(cookie);
						break;
					}
				}
			}

			ra.addFlashAttribute("msg", "비회원 주문 및 결제가 완료되었습니다! 💳");
			return "redirect:/order/nmorderlist";
			
		} else {
			// ⭕ [B. 회원 주문 케이스]
			orderVO.setMemberId(loginId);
			orderVO.setOrderCode("ORDER_" + System.currentTimeMillis());
			orderVO.setDeliveryStatus("결제완료");

			try {
				orderService.processOrder(orderVO);
				
				// 💡 [추가] 주문이 정상 완료되었으므로 회원의 DB 장바구니를 깨끗하게 비웁니다.
				cartService.clearCart(loginId);
				
				ra.addFlashAttribute("msg", "회원 주문 및 결제가 성공적으로 완료되었습니다! 💳");
			} catch (Exception e) {
				e.printStackTrace();
				ra.addFlashAttribute("msg", "주문 처리 중 DB 오류 발생");
			}
			
			return "redirect:/order/list";
		}
	}

	
}