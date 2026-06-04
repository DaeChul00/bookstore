package order.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import order.model.OrderVO;

@Repository
public class OrderDAOH2 {
    @Autowired
    Connection conn;

    // 주문 저장 (장바구니 데이터를 한 줄씩 구매 기록으로 저장)
    public int insertOrder(OrderVO order) {
        String sql = "INSERT INTO ORDERS (member_id, book_id, title, count, order_price, bookimage) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getMemberId());
            ps.setInt(2, order.getBookId());
            ps.setString(3, order.getTitle());
            ps.setInt(4, order.getCount());
            ps.setInt(5, order.getOrderPrice()); // 구매 당시 가격 저장
            ps.setString(6, order.getBookimage());
            return ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; }
    }

    // 주문 내역 조회 (order-list.jsp에서 사용)
    public List<OrderVO> findOrdersByMemberId(String memberId) {
        List<OrderVO> list = new ArrayList<>();
        String sql = "SELECT * FROM ORDERS WHERE member_id = ? ORDER BY order_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(OrderVO.builder()
                    .orderId(rs.getInt("order_id"))
                    .title(rs.getString("title"))
                    .count(rs.getInt("count"))
                    .orderPrice(rs.getInt("order_price"))
                    .bookimage(rs.getString("bookimage"))
                    .orderDate(rs.getString("order_date"))
                    .build());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}