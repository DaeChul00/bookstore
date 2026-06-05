package order.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource; // DataSource 사용을 위해 추가

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import order.model.OrderVO;

@Repository
public class OrderDAOH2 {

    @Autowired
    private DataSource dataSource;

    public int insertOrder(OrderVO order) {
        String sql = "INSERT INTO ORDERS (MEMBER_ID, BOOK_ID, TITLE, COUNT, ORDER_PRICE, BOOKIMAGE) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, order.getMemberId());
            ps.setInt(2, order.getBookId());
            ps.setString(3, order.getTitle());
            ps.setInt(4, order.getCount());
            ps.setInt(5, order.getOrderPrice());
            ps.setString(6, order.getBookimage());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<OrderVO> findOrdersByMemberId(String memberId) {
        List<OrderVO> list = new ArrayList<>();
        String sql = "SELECT * FROM ORDERS WHERE MEMBER_ID = ? ORDER BY ORDER_ID DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderVO order = OrderVO.builder()
                            .orderId(rs.getInt("ORDER_ID"))
                            .memberId(rs.getString("MEMBER_ID"))
                            .bookId(rs.getInt("BOOK_ID"))
                            .title(rs.getString("TITLE"))
                            .count(rs.getInt("COUNT"))
                            .orderPrice(rs.getInt("ORDER_PRICE"))
                            .bookimage(rs.getString("BOOKIMAGE"))
                            // Timestamp -> String 규격 변환부 안전성 유지
                            .orderDate(rs.getTimestamp("ORDER_DATE") != null ? rs.getTimestamp("ORDER_DATE").toString() : null)
                            .build();
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}