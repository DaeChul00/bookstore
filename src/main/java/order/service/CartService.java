package order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import order.model.CartVO;
import order.repository.CartDAOH2;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartDAOH2 cartDAO;

    public boolean addCart(CartVO cart) {
        // 1. 이미 동일한 도서가 장바구니에 적재되어 있는지 검사
        List<CartVO> currentCart = cartDAO.findCartByMemberId(cart.getMemberId());
        
        if (currentCart != null) {
            for (CartVO item : currentCart) {
                if (item.getBookId() == cart.getBookId()) {
                    // 동일한 책이 있으면 수량만 추가 누적 업데이트
                    item.setCount(item.getCount() + cart.getCount());
                    return cartDAO.updateCartCount(item) > 0;
                }
            }
        }
        
        // 2. 없는 도서라면 신규 행으로 인서트 가동
        int result = cartDAO.insertCart(cart);
        return result > 0; 
    }

    public List<CartVO> getCartList(String memberId) {
        return cartDAO.findCartByMemberId(memberId);
    }

    public boolean updateCartCount(String memberId, int bookId, int count) {
        CartVO cart = CartVO.builder()
                            .memberId(memberId)
                            .bookId(bookId)
                            .count(count)
                            .build();
        return cartDAO.updateCartCount(cart) > 0;
    }

    public boolean deleteCart(String memberId, int bookId) {
        return cartDAO.deleteCart(memberId, bookId) > 0;
    }

    public boolean clearCart(String memberId) {
        return cartDAO.clearCart(memberId) > 0;
    }
}