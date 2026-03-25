package order.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import order.model.CartVO;
import order.repository.CartDAO;

@Service
public class CartService {

	@Autowired
	private CartDAO cartDao; // CartDAOH2가 주입됩니다.

	// 1. 장바구니 목록 조회
	public List<CartVO> getCartList(String memberId) {
		return cartDao.findByMemberId(memberId);
	}

	// 2. 장바구니 담기 (기존 로직 유지)
	public boolean addCart(CartVO cart) {
		List<CartVO> remoteList = cartDao.findByMemberId(cart.getMemberId());
		for (CartVO remoteCart : remoteList) {
			if (remoteCart.getBookId() == cart.getBookId()) {
				remoteCart.setCount(remoteCart.getCount() + cart.getCount());
				return cartDao.updateCount(remoteCart) > 0;
			}
		}
		return cartDao.insert(cart) > 0;
	}

	// 3. 장바구니 수량 수정 (추가)
	public boolean updateCartCount(int cartId, int count) {
		// 컨트롤러에서 받은 파라미터로 VO 객체를 생성해 DAO에 전달합니다.
		CartVO cart = CartVO.builder()
				.cartId(cartId)
				.count(count)
				.build();
		return cartDao.updateCount(cart) > 0;
	}

	// 4. 장바구니 개별 삭제 (추가)
	public boolean deleteCart(int cartId) {
		return cartDao.delete(cartId) > 0;
	}

	// 5. 장바구니 전체 비우기 (주문 완료용)
	public boolean clearCart(String memberId) {
		return cartDao.deleteByMemberId(memberId) > 0;
	}
}