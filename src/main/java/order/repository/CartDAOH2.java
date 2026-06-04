package order.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import order.model.CartVO;

@Repository
public class CartDAOH2 implements CartDAO {
	@Autowired
	Connection conn;

	// 1. 장바구니 수량 수정 (Update)
	@Override
	public int updateCount(CartVO cart) {
		// 특정 장바구니 번호(cart_id)의 수량(count)만 변경합니다.
		String sql = "UPDATE CART SET count = ? WHERE cart_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cart.getCount());
			ps.setInt(2, cart.getCartId());
			return ps.executeUpdate(); // 성공 시 1, 실패 시 0 반환
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 2. 장바구니 개별 항목 삭제 (Delete)
	@Override
	public int delete(int cartId) {
		// 사용자가 선택한 특정 장바구니 아이템만 삭제합니다.
		String sql = "DELETE FROM CART WHERE cart_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cartId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// --- 기존 메서드들 (생략 가능하나 구조 확인용) ---

	public int insert(CartVO cart) {
		String sql = "INSERT INTO CART (member_id, book_id, count) VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cart.getMemberId());
			ps.setInt(2, cart.getBookId());
			ps.setInt(3, cart.getCount());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<CartVO> findByMemberId(String memberId) {
		List<CartVO> list = new ArrayList<>();
		String sql = "SELECT c.*, b.title, b.price, b.bookimage FROM CART c "
				+ "JOIN BOOK b ON c.book_id = b.id WHERE c.member_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, memberId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(CartVO.builder().cartId(rs.getInt("cart_id")).bookId(rs.getInt("book_id"))
						.count(rs.getInt("count")).title(rs.getString("title")).price(rs.getInt("price"))
						.bookimage(rs.getString("bookimage")).build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int deleteByMemberId(String memberId) {
		String sql = "DELETE FROM CART WHERE member_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, memberId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
}