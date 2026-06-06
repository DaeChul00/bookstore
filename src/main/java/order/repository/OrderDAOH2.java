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

    public List<OrderVO> findOrdersByMemberId(String memberId) {
        List<OrderVO> list = new ArrayList<>();
        
        // [핵심] 같은 책(BOOK_ID)은 한 줄로 묶고 수량과 금액을 합산(SUM)하되, BOOK_ID 정보도 함께 셀렉트합니다.
        String sql = "SELECT BOOK_ID, TITLE, BOOKIMAGE, SUM(COUNT) AS TOTAL_COUNT, " +
                     "SUM(ORDER_PRICE) AS TOTAL_PRICE, MAX(ORDER_DATE) AS LATEST_DATE " +
                     "FROM ORDERS " +
                     "WHERE MEMBER_ID = ? " +
                     "GROUP BY BOOK_ID, TITLE, BOOKIMAGE " +
                     "ORDER BY LATEST_DATE DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(OrderVO.builder()
                        .bookId(rs.getInt("BOOK_ID")) // 🚨 이제 0이 아닌 진짜 책 ID가 담깁니다!
                        .title(rs.getString("TITLE"))
                        .bookimage(rs.getString("BOOKIMAGE"))
                        .count(rs.getInt("TOTAL_COUNT"))       // 합산된 수량
                        .orderPrice(rs.getInt("TOTAL_PRICE"))   // 합산된 총 결제 금액
                        .orderDate(rs.getString("LATEST_DATE")) // 가장 최근 주문 일자
                        .build());
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}