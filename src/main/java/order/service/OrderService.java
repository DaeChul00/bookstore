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

    @Transactional
    public boolean placeOrder(String memberId, List<OrderVO> orderList) {
        int resultCount = 0;
        for (OrderVO order : orderList) {
            order.setMemberId(memberId);
            resultCount += orderDao.insertOrder(order);
        }
        
        if (resultCount == orderList.size()) {
            cartDao.clearCart(memberId);
            return true;
        }
        return false;
    }

    public List<OrderVO> findOrdersByMemberId(String memberId) {
        return orderDao.findOrdersByMemberId(memberId);
    }
	
    public String getOrderStatus(int orderId) {
        return orderDao.getDeliveryStatus(orderId);
    }
	
    public OrderVO getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }
    
    // 토스 결제 승인 후 최종 장바구니 리스트 DB 마이그레이션 적재 아키텍처
    @Transactional
    public void confirmPayment(String orderCode, String memberId, String zipcode, String roadAddress) {
        // 1. 기존에 등록된 기본 배송지가 있다면 'N'으로 사전 해제 (희조 인프라 융합)
        orderDao.disableDefaultAddress(memberId);
        
        // 2. 장바구니 리스트를 긁어와서 토스 코드 및 주소록 매핑 바인딩
        List<CartVO> cartList = cartDao.findCartByMemberId(memberId);
        
        for (CartVO cart : cartList) {
            OrderVO order = OrderVO.builder()
                .memberId(memberId)
                .bookId(cart.getBookId())
                .title(cart.getTitle())
                .count(cart.getCount())
                .orderPrice(cart.getPrice() * cart.getCount())
                .bookimage(cart.getBookimage())
                .deliveryStatus("결제완료")
                .orderCode(orderCode)
                .zipcode(zipcode)         // 희조 데이터 퓨전
                .roadAddress(roadAddress) // 희조 데이터 퓨전
                .build();
            orderDao.insertOrder(order);
        }
        
        // 3. 주문이 완료되었으므로 회원의 DB 장바구니 일괄 삭제
        cartDao.clearCart(memberId);
    }

    public List<OrderVO> findNonMemberOrders(Integer guestOrderId) {
        return orderDao.findNonMemberOrders(guestOrderId); 
    }

    public int nonlogInsert(OrderVO orderVO) {
        return orderDao.nonlogInsert(orderVO); 
    }
}