package order.controller;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import order.model.CartVO;
import order.model.OrderVO;
import order.service.CartService;
import order.service.OrderService;
import book.model.BookVO;
import book.service.BookService; // 💡 비회원 도서 정보 조회를 위해 임포트 추가

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService; // 💡 비회원 쿠키 장바구니 화면 노출용 도서 서비스 주입

    // 시큐리티 금고에서 로그인된 회원의 진짜 아이디를 안전하게 추출하는 공통 메서드
    private String getLoginId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return auth.getName();
    }

    // 1. 장바구니 목록 보기 (cart.jsp 연결 + 비회원 쿠키 파싱 완벽 복구)
    @RequestMapping("/cart")
    public String cartList(HttpServletRequest request, Model model) {
        String mid = getLoginId();
        
        if (mid != null) {
            // ⭕ [회원 로그인 상태]: 기존처럼 데이터베이스(DB)에서 조회
            model.addAttribute("cartList", cartService.getCartList(mid));
        } else {
            // ⭕ [비회원 로그아웃 상태]: 브라우저의 guestCart 쿠키 가공 처리 후 바인딩
            List<CartVO> guestCartList = new ArrayList<>();
            Cookie[] cookies = request.getCookies();
            
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("guestCart".equals(cookie.getName())) {
                        try {
                            // URL 인코딩된 쿠키 문자열 복원
                            String cartJson = java.net.URLDecoder.decode(cookie.getValue(), "UTF-8");
                            
                            // 대괄호[], 중괄호{} 텍스트 분해 유틸 로직
                            String cleanJson = cartJson.replace("[", "").replace("]", "");
                            if (!cleanJson.trim().isEmpty()) {
                                String[] items = cleanJson.split("\\},\\s*\\{");
                                
                                for (String item : items) {
                                    String refinedItem = item.replace("{", "").replace("}", "");
                                    String[] subParts = refinedItem.split(",");
                                    int bookId = 0;
                                    int count = 0;
                                    
                                    for (String part : subParts) {
                                        if (part.contains("bookId")) {
                                            bookId = Integer.parseInt(part.replaceAll("[^0-9]", "").trim());
                                        } else if (part.contains("count")) {
                                            count = Integer.parseInt(part.replaceAll("[^0-9]", "").trim());
                                        }
                                    }
                                    
                                    // 파싱 성공 시 BookService에서 책 세부정보(제목, 가격, 이미지)를 실시간 바인딩
                                    if (bookId > 0 && count > 0) {
                                        BookVO bookInfo = bookService.getBook(bookId);
                                        if (bookInfo != null) {
                                            guestCartList.add(CartVO.builder()
                                                    .bookId(bookId)
                                                    .count(count)
                                                    .title(bookInfo.getTitle())
                                                    .price(bookInfo.getPrice())
                                                    .bookimage(bookInfo.getBookimage())
                                                    .build());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("❌ OrderController 비회원 쿠키 변환 중 예외: " + e.getMessage());
                        }
                    }
                }
            }
            // 쿠키에서 뽑아낸 목록을 화면(Model)에 전달
            model.addAttribute("cartList", guestCartList);
        }
        
        model.addAttribute("contentPage", "/WEB-INF/views/order/cart.jsp");
        return "layout/layout";
    }

    // 2. 장바구니에 상품 추가
    @RequestMapping("/addCart")
    public String addCart(CartVO cart, RedirectAttributes ra) {
        String mid = getLoginId();
        if (mid == null) return "redirect:/login";

        cart.setMemberId(mid);
        if (cartService.addCart(cart)) {
            ra.addFlashAttribute("msg", "장바구니에 담겼습니다.");
        }
        return "redirect:/order/cart";
    }

    // 3. 장바구니 수량 변경
    @PostMapping("/updateCartAsync")
    @ResponseBody
    public Map<String, String> updateCartAsync(@RequestParam("cartId") int bookId, @RequestParam("count") int count) {
        Map<String, String> result = new HashMap<>();
        String mid = getLoginId();
        
        if (mid == null) {
            result.put("status", "fail");
            return result;
        }
        
        boolean ok = cartService.updateCartCount(mid, bookId, count);
        result.put("status", ok ? "success" : "fail");
        return result;
    }

    // 4. 장바구니 항목 개별 삭제
    @RequestMapping(value = "/deleteCart", method = RequestMethod.POST)
    public String deleteCart(@RequestParam("cartId") int bookId) {
        String mid = getLoginId();
        if (mid != null) {
            cartService.deleteCart(mid, bookId);
        }
        return "redirect:/order/cart";
    }

    // 5. 주문하기 (구매 프로세스 가동 시 조원이 추가한 배송 준비중 상태 기본 세팅)
    @RequestMapping("/buy")
    public String buy() {
        String mid = getLoginId();
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
                        .deliveryStatus("주문완료") // 초기 배송 상태 부여
                        .build());
            }
            orderService.placeOrder(mid, orderList);
            return "redirect:/order/list";
        }
        return "redirect:/order/cart";
    }

    // 6. 주문 내역 보기 (order-list.jsp 연결)
    @RequestMapping("/list")
    public String orderList(Model model) {
        String mid = getLoginId();
        if (mid == null) return "redirect:/login";

        model.addAttribute("orderList", orderService.getOrderList(mid));
        model.addAttribute("contentPage", "/WEB-INF/views/order/order-list.jsp");
        return "layout/layout";
    }
}