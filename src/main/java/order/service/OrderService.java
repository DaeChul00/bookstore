package order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import order.model.CartVO;
import order.model.OrderVO;
import order.repository.CartDAO;
import order.repository.OrderDAOH2;

@Service
public class OrderService {
	@Autowired
	private OrderDAOH2 orderDao;
	@Autowired
	private CartDAO cartDao;

	// 주문 처리: 장바구니 데이터를 주문으로 옮기고 비우기
	@Transactional // 둘 중 하나라도 실패하면 안 되므로 붙여주는 것이 좋습니다.
	public void processOrder(String memberId, List<CartVO> cartList) {
		for (CartVO cart : cartList) {
			OrderVO order = OrderVO.builder()
					.memberId(memberId)
					.bookId(cart.getBookId())
					.title(cart.getTitle())
					.count(cart.getCount())
					.orderPrice(cart.getPrice())
					.bookimage(cart.getBookimage())
					.build();
			orderDao.insertOrder(order);
		}
		cartDao.deleteByMemberId(memberId);
	}

	public List<OrderVO> getOrderList(String memberId) {
		return orderDao.findOrdersByMemberId(memberId);
	}
}
