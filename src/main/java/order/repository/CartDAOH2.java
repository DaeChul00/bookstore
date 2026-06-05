package order.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import order.model.CartVO;

@Repository
public class CartDAOH2 {

    @Autowired
    private DataSource dataSource;

    public int insertCart(CartVO cart) {
        String sql = "INSERT INTO CART (MEMBER_ID, BOOK_ID, COUNT) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cart.getMemberId());
            ps.setInt(2, cart.getBookId());
            ps.setInt(3, cart.getCount());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<CartVO> findCartByMemberId(String memberId) {
        List<CartVO> list = new ArrayList<>();
        // 💡 대철이의 CART 테이블과 BOOK 테이블을 복합 조인해서 화면 표기용 가방을 두둑하게 채웁니다!
        String sql = "SELECT C.CART_ID, C.MEMBER_ID, C.BOOK_ID, C.COUNT, "
                   + "B.TITLE, B.PRICE, B.BOOKIMAGE "
                   + "FROM CART C "
                   + "JOIN BOOK B ON C.BOOK_ID = B.ID "
                   + "WHERE C.MEMBER_ID = ? "
                   + "ORDER BY C.CART_ID DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartVO cart = CartVO.builder()
                            .cartId(rs.getInt("CART_ID"))
                            .memberId(rs.getString("MEMBER_ID"))
                            .bookId(rs.getInt("BOOK_ID"))
                            .count(rs.getInt("COUNT"))
                            .title(rs.getString("TITLE"))
                            .price(rs.getInt("PRICE"))
                            .bookimage(rs.getString("BOOKIMAGE"))
                            .build();
                    list.add(cart);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int updateCartCount(CartVO cart) {
        String sql = "UPDATE CART SET COUNT = ? WHERE MEMBER_ID = ? AND BOOK_ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cart.getCount());
            ps.setString(2, cart.getMemberId());
            ps.setInt(3, cart.getBookId());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int deleteCart(String memberId, int bookId) {
        String sql = "DELETE FROM CART WHERE MEMBER_ID = ? AND BOOK_ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, memberId);
            ps.setInt(2, bookId);
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int clearCart(String memberId) {
        String sql = "DELETE FROM CART WHERE MEMBER_ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, memberId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}