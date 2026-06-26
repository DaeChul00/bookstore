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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import book.model.BookVO;
import book.service.BookService;
import order.model.CartVO;
import order.model.OrderVO;
import order.service.CartService;
import order.service.OrderService;
import order.service.ReviewService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                            List<java.util.Map<String, Object>> list = mapper.readValue(decodedCookie, new TypeReference<List<java.util.Map<String, Object>>>() {});
                            
                            for (java.util.Map<String, Object> map : list) {
                                int bookId = (Integer) map.get("bookId");
                                int count = (Integer) map.get("count");
                                BookVO book = bookService.getBook(bookId);
                                if (book != null) {
                                    guestCartList.add(CartVO.builder()
                                            .bookId(bookId)
                                            .count(count)
                                            .title(book.getTitle())
                                            .price(book.getPrice())
                                            .bookimage(book.getBookimage())
                                            .build());
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
    public String addCart(@RequestParam("bookId") int bookId, 
                          @RequestParam("count") int count,
                          HttpServletRequest request, 
                          HttpServletResponse response) {
        
        String mid = getSecurityLoginId();
        
        if (mid != null) {
            CartVO cart = CartVO.builder()
                            .memberId(mid)
                            .bookId(bookId)
                            .count(count)
                            .build();
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
                            cartList = mapper.readValue(guestCartValue, new TypeReference<List<java.util.Map<String, Object>>>() {});
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
		if (mid == null) return "login_required";
		
		boolean ok = cartService.updateCartCount(mid, bookId, count);
		return ok ? "success" : "fail";
	}

    // 4. 장바구니 삭제
    @RequestMapping("/deleteCart")
    public String deleteCart(@RequestParam("cartId") int bookId) {
        String mid = getSecurityLoginId();
        if (mid == null) return "redirect:/login";
        
        cartService.deleteCart(mid, bookId);
        return "redirect:/order/cart";
    }

    // 5. 결제 및 주문완료 프로세스
    @RequestMapping("/buy")
    public String buy() {
        String mid = getSecurityLoginId();
        if (mid == null) return "redirect:/login";
        
        List<CartVO> cartList = cartService.getCartList(mid);
        if (cartList != null && !cartList.isEmpty()) {
            List<OrderVO> orderList = new ArrayList<>();
            for (CartVO cart : cartList) {
                orderList.add(OrderVO.builder()
                        .memberId(mid)
                        .bookId(cart.getBookId())
                        .title(cart.getTitle())
                        .count(cart.getCount())
                        .orderPrice(cart.getPrice() * cart.getCount())
                        .bookimage(cart.getBookimage())
                        .deliveryStatus("주문완료")
                        .build());
            }
            orderService.placeOrder(mid, orderList);
            return "redirect:/order/list";
        }
        return "redirect:/order/cart";
    }

    // 6. 나의 주문 내역 조회
    @RequestMapping("/list")
    public String orderList(Model model) {
        String mid = getSecurityLoginId();
        if (mid == null) return "redirect:/login";
        
        List<OrderVO> orderList = orderService.findOrdersByMemberId(mid);
        model.addAttribute("orderList", orderList);
        model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
        return "layout/layout";
    }
    
    // 7. 한줄평 리뷰 등록 가드
    @ResponseBody
    @RequestMapping(value = "/review-insert", method = RequestMethod.POST)
    public String insertReview(@RequestParam("bookId") int bookId,
                               @RequestParam("rating") int rating,
                               @RequestParam("content") String content) {
        String mid = getSecurityLoginId();
        if (mid == null) return "login_required";
        
        boolean isDuplicate = bookService.hasAlreadyReviewed(bookId, mid);
        if (isDuplicate) return "already_exists";
        
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
    
 // 1. [결제 전] 주문 데이터 임시 생성 (DB에 '결제대기' 상태로 저장)
    @PostMapping("/prepare")
    @ResponseBody
    public Map<String, Object> prepareOrder() {
        String mid = getSecurityLoginId(); // 사용자가 구현한 로그인 ID 추출 메서드
        List<CartVO> cartList = cartService.getCartList(mid);
        int total = cartList.stream().mapToInt(c -> c.getPrice() * c.getCount()).sum();
        
        String orderCode = "ORDER_" + System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderCode);
        response.put("totalPrice", total);
        response.put("orderName", cartList.get(0).getTitle() + (cartList.size() > 1 ? " 외 " + (cartList.size()-1) + "건" : ""));
        return response;
    }

    // 2. [결제 후] 승인 처리 (토스 결제 완료 후 리다이렉트)
    @RequestMapping("/success")
    public String orderSuccess(@RequestParam String paymentKey, @RequestParam String orderId) {
        String mid = getSecurityLoginId();
        orderService.confirmPayment(orderId, mid);
        return "redirect:/order/list";
    }
}