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
import org.springframework.web.bind.annotation.RequestBody;
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
    @SuppressWarnings("unchecked")
    public Map<String, Object> prepareOrder(@RequestBody Map<String, Object> payload, HttpSession session) {
        String mid = getSecurityLoginId();
        
        int total = 0;
        String firstTitle = "";
        int totalItemsSize = 0;
        
        List<OrderVO> tempOrders = new ArrayList<>();
        
        if (mid != null) {
            // [회원] DB 장바구니 기준 연산
            List<CartVO> cartList = cartService.getCartList(mid);
            if (cartList != null && !cartList.isEmpty()) {
                total = cartList.stream().mapToInt(c -> c.getPrice() * c.getCount()).sum();
                firstTitle = cartList.get(0).getTitle();
                totalItemsSize = cartList.size();
            }
        } else {
            // [비회원] JSP에서 보낸 장바구니 객체 목록을 가공하여 가상 OrderVO 정보 세팅
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) payload.get("cartItems");
            if (cartItems != null && !cartItems.isEmpty()) {
                for (Map<String, Object> item : cartItems) {
                    int price = Integer.parseInt(String.valueOf(item.get("price")));
                    int count = Integer.parseInt(String.valueOf(item.get("count")));
                    total += (price * count);
                    
                    // 비회원 정보 파싱 및 임시 객체 빌드
                    OrderVO vo = OrderVO.builder()
                            .bookId(Integer.parseInt(String.valueOf(item.get("bookId"))))
                            .title(String.valueOf(item.get("title")))
                            .count(count)
                            .orderPrice(price * count)
                            .bookimage(String.valueOf(item.get("bookimage")))
                            .zipcode(String.valueOf(payload.get("zipcode")))
                            .roadAddress(String.valueOf(payload.get("roadAddress")))
                            .build();
                    tempOrders.add(vo);
                }
                firstTitle = String.valueOf(cartItems.get(0).get("title"));
                totalItemsSize = cartItems.size();
                
                // 💡 [핵심 가드] 아직 최종 승인이 안 났으므로, 생성한 임시 비회원 주문 데이터를 
                // 결제 완료(/success) 시점에 꺼내 쓰도록 세션에 임시 보관해 둡니다.
                session.setAttribute("tempGuestOrders", tempOrders);
            }
        }
        
        String orderCode = "ORDER_" + System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderCode);
        response.put("totalPrice", total);
        response.put("orderName", firstTitle + (totalItemsSize > 1 ? " 외 " + (totalItemsSize - 1) + "건" : ""));
        
        return response;
    }

    // 2. 토스 결제 최종 성공 콜백 (비회원 데이터 영구 등록 및 세션 주입)
    @RequestMapping("/success")
    public String orderSuccess(@RequestParam String paymentKey, @RequestParam String orderId,
                               @RequestParam(value="zipcode", required=false, defaultValue="") String zipcode,
                               @RequestParam(value="roadAddress", required=false, defaultValue="") String roadAddress,
                               HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) {
        
        String mid = getSecurityLoginId();
        
        if (mid != null) {
            // [회원] 기존 서비스 로직 수행
            orderService.confirmPayment(orderId, mid, zipcode, roadAddress);
            return "redirect:/order/list";
        } else {
            // ⭕ [비회원] 결제 전 /prepare 단계에서 세션에 박아둔 임시 주문 데이터 획득
            List<OrderVO> tempOrders = (List<OrderVO>) session.getAttribute("tempGuestOrders");
            
            if (tempOrders != null && !tempOrders.isEmpty()) {
                int lastGeneratedKey = 0;
                
                // 장바구니에 담긴 상품 수만큼 ORDERS 테이블에 인서트 수행
                for (OrderVO orderVO : tempOrders) {
                    // 주소 파라미터 보정 재확인 주입
                    orderVO.setZipcode(zipcode);
                    orderVO.setRoadAddress(roadAddress);
                    
                    // 💡 작성해주신 nonlogInsert 호출 -> H2 DB에 등록되고 생성된 AUTO_INCREMENT 'ORDER_ID' 반환됨
                    lastGeneratedKey = orderService.nonlogInsert(orderVO);
                }
                
                // 💡 [결정적 해결책] 생성된 최종 키를 세션에 저장하여 nmorderlist 조회 쿼리가 읽을 수 있게 바인딩!
                session.setAttribute("guestOrderId", lastGeneratedKey);
                
                // 사용이 끝난 임시 세션 가드는 제거
                session.removeAttribute("tempGuestOrders");
                
                // 🍪 비회원 장바구니 쿠키(guestCart) 완벽 초기화 파괴 조치
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
        }
        
        return "redirect:/order/cart";
    }

}