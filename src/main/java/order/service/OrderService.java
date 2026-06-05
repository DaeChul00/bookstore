package order.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import order.model.CartVO;
import order.model.OrderVO;
import order.repository.CartDAOH2;  // 💡 인터페이스 대신 실제 클래스 CartDAOH2 임포트
import order.repository.OrderDAOH2;

@Service
public class OrderService {

	@Autowired
	private OrderDAOH2 orderDao;

	@Autowired
	private CartDAOH2 cartDao;

	// 1. 주문 생성 로직 (장바구니 연동 규격 맞춤 완료)
	public boolean placeOrder(String memberId, List<OrderVO> orderList) {
		int resultCount = 0;
		for (OrderVO order : orderList) {
			order.setMemberId(memberId);
			resultCount += orderDao.insertOrder(order);
		}
		
		// 주문이 성공적으로 들어갔다면, 장바구니를 깨끗하게 비워줍니다.
		if (resultCount == orderList.size()) {
			cartDao.clearCart(memberId); // 메서드명 싱크 완료
			return true;
		}
		return false;
	}

	// 2. 회원별 주문 내역 조회
	public List<OrderVO> getOrderList(String memberId) {
		return orderDao.findOrdersByMemberId(memberId);
	}
}