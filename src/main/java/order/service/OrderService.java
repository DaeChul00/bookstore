package order.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import order.model.OrderVO;
import order.repository.CartDAOH2;
import order.repository.OrderDAOH2;

@Service
public class OrderService {

    @Autowired
    private OrderDAOH2 orderDao;

    @Autowired
    private CartDAOH2 cartDao;

    // 주문 완료 후 장바구니 비우기 트랜잭션 병합 완료
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

    public List<OrderVO> findOrdersByMemberId(String memberId) {
        return orderDao.findOrdersByMemberId(memberId);
    }
}