package order.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import order.model.OrderVO;

@Repository
public class OrderDAOH2 {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. 주문 저장 (배송 상태 '주문완료' 기본 부여)
    public int insertOrder(OrderVO order) {
        String sql = "INSERT INTO ORDERS (member_id, book_id, title, count, order_price, bookimage, delivery_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String status = (order.getDeliveryStatus() != null) ? order.getDeliveryStatus() : "주문완료";
        
        return jdbcTemplate.update(sql, 
            order.getMemberId(), 
            order.getBookId(), 
            order.getTitle(), 
            order.getCount(), 
            order.getOrderPrice(), 
            order.getBookimage(), 
            status
        );
    }

    // 2. 일반 유저용 본인 마이페이지 주문 내역 조회 (중복 서브 묶음 연산 구조)
    public List<OrderVO> findOrdersByMemberId(String memberId) {
        String sql = "SELECT BOOK_ID, TITLE, BOOKIMAGE, SUM(COUNT) AS TOTAL_COUNT, " +
                     "SUM(ORDER_PRICE) AS TOTAL_PRICE, MAX(ORDER_DATE) AS LATEST_DATE " +
                     "FROM ORDERS " +
                     "WHERE MEMBER_ID = ? " +
                     "GROUP BY BOOK_ID, TITLE, BOOKIMAGE " +
                     "ORDER BY LATEST_DATE DESC";
        
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> OrderVO.builder()
                .bookId(rs.getInt("BOOK_ID"))
                .title(rs.getString("TITLE"))
                .bookimage(rs.getString("BOOKIMAGE"))
                .count(rs.getInt("TOTAL_COUNT"))
                .orderPrice(rs.getInt("TOTAL_PRICE"))
                .orderDate(rs.getTimestamp("LATEST_DATE") != null ? rs.getTimestamp("LATEST_DATE").toString() : null)
                .build(), memberId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 3. 관리자 대시보드용 원상 복구 전체 내역 조회 API
    public List<OrderVO> findAllOrdersForAdmin() {
        String sql = "SELECT * FROM ORDERS ORDER BY order_date DESC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> OrderVO.builder()
                .orderId(rs.getInt("order_id"))
                .memberId(rs.getString("member_id"))
                .bookId(rs.getInt("book_id"))
                .title(rs.getString("title"))
                .count(rs.getInt("count"))
                .orderPrice(rs.getBigDecimal("order_price").intValue())
                .bookimage(rs.getString("bookimage"))
                .orderDate(rs.getTimestamp("order_date") != null ? rs.getTimestamp("order_date").toString() : null)
                .deliveryStatus(rs.getString("delivery_status"))
                .build());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 4. 관리자용 배송 상태 스위칭 변경 API 
    public int updateDeliveryStatus(int orderId, String status) {
        String sql = "UPDATE ORDERS SET delivery_status = ? WHERE order_id = ?";
        return jdbcTemplate.update(sql, status, orderId);
    }
}