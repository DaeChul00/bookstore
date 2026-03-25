package order.repository;

import java.util.List;
import order.model.CartVO;

public interface CartDAO {
    // 1. 장바구니 담기 (추가)
    public int insert(CartVO cart);
    
    // 2. 특정 사용자의 장바구니 목록 보기
    public List<CartVO> findByMemberId(String memberId);
    
    // 3. 수량 변경 (업데이트)
    public int updateCount(CartVO cart);
    
    // 4. 장바구니 항목 삭제
    public int delete(int cartId);
    
    // 5. 주문 완료 후 장바구니 비우기
    public int deleteByMemberId(String memberId);
}