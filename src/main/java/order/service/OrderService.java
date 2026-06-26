package order.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import order.model.CartVO;
import order.model.OrderVO;
import order.repository.CartDAOH2;
import order.repository.OrderDAOH2;

@Service
public class OrderService {

    @Autowired
    private OrderDAOH2 orderDao;

    @Autowired
    private CartDAOH2 cartDao;

    // 주문 완료 후 장바구니 일괄 비우기 트랜잭션 인프라 완벽 보존
    @Transactional
    public boolean placeOrder(String memberId, List<OrderVO> orderList) {
        int resultCount = 0;
        for (OrderVO order : orderList) {
            order.setMemberId(memberId);
            resultCount += orderDao.insertOrder(order);
        }
        
        if (resultCount == orderList.size()) {
            cartDao.clearCart(memberId); // 장바구니 일괄 삭제 가동
            return true;
        }
        return false;
    }

    // 일반 유저 마이페이지 주문 리스트 조회 메서드 보존
    public List<OrderVO> findOrdersByMemberId(String memberId) {
        return orderDao.findOrdersByMemberId(memberId);
    }
	
    // 실시간 배송 상태 비동기 갱신용 서비스 엔진
    public String getOrderStatus(int orderId) {
        return orderDao.getDeliveryStatus(orderId);
    }
	
    // 배송 정보 상세 페이지 단건 조회용 서비스 엔진
    public OrderVO getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }
    
    @Transactional
    public void confirmPayment(String orderCode, String memberId) {
        // 1. 장바구니 리스트를 가져와서
        List<CartVO> cartList = cartDao.findCartByMemberId(memberId);
        
        // 2. 루프를 돌며 ORDERS 테이블에 저장해야 합니다.
        for (CartVO cart : cartList) {
            OrderVO order = OrderVO.builder()
                .memberId(memberId)
                .bookId(cart.getBookId())
                .title(cart.getTitle())
                .count(cart.getCount())
                .orderPrice(cart.getPrice() * cart.getCount())
                .bookimage(cart.getBookimage())
                .deliveryStatus("결제완료")
                .orderCode(orderCode) // 여기서 생성된 orderCode 저장
                .build();
            orderDao.insertOrder(order);
        }
        
        // 3. 다 끝난 후 장바구니 비우기
        cartDao.clearCart(memberId);
    }
    
}