package order.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.model.BookVO;
import book.service.BookService;
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

	private String getSecurityLoginId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return null;
		}
		return auth.getName();
	}

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
							List<Map<String, Object>> list = mapper.readValue(decodedCookie,
									new TypeReference<List<Map<String, Object>>>() {});

							for (Map<String, Object> map : list) {
								int bookId = (Integer) map.get("bookId");
								int count = (Integer) map.get("count");
								BookVO book = bookService.getBook(bookId);
								if (book != null) {
									guestCartList.add(CartVO.builder().bookId(bookId).count(count).title(book.getTitle())
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
			List<Map<String, Object>> cartList = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();

			try {
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if ("guestCart".equals(cookie.getName())) {
							guestCartValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
							cartList = mapper.readValue(guestCartValue,
									new TypeReference<List<Map<String, Object>>>() {});
							break;
						}
					}
				}

				boolean isExist = false;
				for (Map<String, Object> map : cartList) {
					if ((Integer) map.get("bookId") == bookId) {
						map.put("count", (Integer) map.get("count") + count);
						isExist = true;
						break;
					}
				}

				if (!isExist) {
					Map<String, Object> newProduct = new HashMap<>();
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

	@ResponseBody
	@RequestMapping(value = "/updateCartAsync", method = RequestMethod.POST)
	public String updateCartAsync(@RequestParam("bookId") int bookId, @RequestParam("count") int count) {
		String mid = getSecurityLoginId();
		if (mid == null) return "login_required";

		boolean ok = cartService.updateCartCount(mid, bookId, count);
		return ok ? "success" : "fail";
	}

	@RequestMapping("/deleteCart")
	public String deleteCart(@RequestParam("cartId") int bookId) {
		String mid = getSecurityLoginId();
		if (mid == null) return "redirect:/login";

		cartService.deleteCart(mid, bookId);
		return "redirect:/order/cart";
	}

	@RequestMapping("/nmorderlist")
	public String nonMemberOrderList(HttpSession session, Model model) {
        Integer guestOrderId = (Integer) session.getAttribute("guestOrderId");
        if (guestOrderId != null) {
            List<OrderVO> orderList = orderService.findNonMemberOrders(guestOrderId);
            model.addAttribute("orderList", orderList);
        } else {
            model.addAttribute("orderList", new ArrayList<OrderVO>());
        }
        model.addAttribute("contentPage", "/WEB-INF/views/order/nmorderlist.jsp");
        model.addAttribute("showBanner", false); 
        return "layout/layout";
	}

	@RequestMapping("/list")
	public String orderList(Model model) {
		String mid = getSecurityLoginId();
		if (mid == null) return "redirect:/login";

		List<OrderVO> orderList = orderService.findOrdersByMemberId(mid);
		model.addAttribute("orderList", orderList);
		model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
		return "layout/layout";
	}

	@ResponseBody
	@RequestMapping(value = "/review-insert", method = RequestMethod.POST)
	public String insertReview(@RequestParam("bookId") int bookId, @RequestParam("rating") int rating,
			@RequestParam("content") String content) {
		String mid = getSecurityLoginId();
		if (mid == null) return "login_required";

		boolean isDuplicate = bookService.hasAlreadyReviewed(bookId, mid);
		if (isDuplicate) return "already_exists";

		boolean success = reviewService.addReview(bookId, mid, rating, content);
		return success ? "success" : "fail";
	}

	@RequestMapping(value = "/status", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public String getOrderStatus(@RequestParam int orderId) {
		return orderService.getOrderStatus(orderId);
	}

	@RequestMapping("/detail")
	public String orderDetail(@RequestParam int orderId, Model model) {
		OrderVO order = orderService.getOrderById(orderId);
		model.addAttribute("order", order);
		model.addAttribute("contentPage", "/WEB-INF/views/order/order_detail.jsp");
		return "layout/layout";
	}
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public String orderSubmit(@ModelAttribute OrderVO orderVO, HttpSession session, 
	                          HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) {
		
		String loginId = getSecurityLoginId();

		if (loginId == null) {
			orderVO.setMemberId(null);
			orderVO.setDeliveryStatus("결제완료");

			int generatedOrderId = orderService.nonlogInsert(orderVO);
			session.setAttribute("guestOrderId", generatedOrderId);

			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("guestCart".equals(cookie.getName())) {
						cookie.setValue("");
						cookie.setPath("/");
						cookie.setMaxAge(0);
						response.addCookie(cookie);
						break;
					}
				}
			}
			ra.addFlashAttribute("msg", "비회원 주문 및 결제가 완료되었습니다! 💳");
			return "redirect:/order/nmorderlist";
		}
		return "redirect:/order/cart";
	}

    @PostMapping("/prepare")
    @ResponseBody
    public Map<String, Object> prepareOrder() {
        String mid = getSecurityLoginId();
        List<CartVO> cartList = cartService.getCartList(mid);
        int total = cartList.stream().mapToInt(c -> c.getPrice() * c.getCount()).sum();
        
        String orderCode = "ORDER_" + System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderCode);
        response.put("totalPrice", total);
        response.put("orderName", cartList.get(0).getTitle() + (cartList.size() > 1 ? " 외 " + (cartList.size()-1) + "건" : ""));
        return response;
    }

    @RequestMapping("/success")
    public String orderSuccess(@RequestParam String paymentKey, @RequestParam String orderId,
                               @RequestParam(value="zipcode", required=false, defaultValue="") String zipcode,
                               @RequestParam(value="roadAddress", required=false, defaultValue="") String roadAddress) {
        String mid = getSecurityLoginId();
        // 찬영님의 토스 승인 인프라와 희조님의 주소 데이터 트랜잭션 병합 호출
        orderService.confirmPayment(orderId, mid, zipcode, roadAddress);
        return "redirect:/order/list";
    }
}