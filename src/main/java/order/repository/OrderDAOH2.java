package order.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import order.model.OrderVO;

@Repository
public class OrderDAOH2 {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 주문 저장 (기본 배송 상태 '주문완료'로 세팅)
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

    // 일반 회원용 본인 주문 내역 조회
    public List<OrderVO> findOrdersByMemberId(String memberId) {
        String sql = "SELECT * FROM ORDERS WHERE member_id = ? ORDER BY order_id DESC";
        try {
            return jdbcTemplate.query(sql, new RowMapper<OrderVO>() {
                @Override
                public OrderVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return OrderVO.builder()
                            .orderId(rs.getInt("order_id"))
                            .memberId(rs.getString("member_id"))
                            .bookId(rs.getInt("book_id"))
                            .title(rs.getString("title"))
                            .count(rs.getInt("count"))
                            .orderPrice(rs.getBigDecimal("order_price").intValue())
                            .bookimage(rs.getString("bookimage"))
                            .orderDate(rs.getTimestamp("order_date") != null ? rs.getTimestamp("order_date").toString() : null)
                            .deliveryStatus(rs.getString("delivery_status"))
                            .build();
                }
            }, memberId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // =================================================================
    // 관리자 전용 전체 회원 주문 목록 조회 (최신순)
    // =================================================================
    public List<OrderVO> findAllOrdersForAdmin() {
        String sql = "SELECT * FROM ORDERS ORDER BY order_date DESC";
        try {
            return jdbcTemplate.query(sql, new RowMapper<OrderVO>() {
                @Override
                public OrderVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return OrderVO.builder()
                            .orderId(rs.getInt("order_id"))
                            .memberId(rs.getString("member_id"))
                            .title(rs.getString("title"))
                            .count(rs.getInt("count"))
                            .orderPrice(rs.getBigDecimal("order_price").intValue())
                            .bookimage(rs.getString("bookimage"))
                            .orderDate(rs.getTimestamp("order_date") != null ? rs.getTimestamp("order_date").toString() : null)
                            .deliveryStatus(rs.getString("delivery_status"))
                            .build();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =================================================================
    // 관리자용 특정 주문의 배송 상태 변경 처리
    // =================================================================
    public int updateDeliveryStatus(int orderId, String status) {
        String sql = "UPDATE ORDERS SET delivery_status = ? WHERE order_id = ?";
        return jdbcTemplate.update(sql, status, orderId);
    }
}