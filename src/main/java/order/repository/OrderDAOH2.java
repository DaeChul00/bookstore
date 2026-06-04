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
        String sql = "INSERT INTO ORDERS (member_id, book_id, title, count, order_price, bookimage, delivery_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getMemberId());
            ps.setInt(2, order.getBookId());
            ps.setString(3, order.getTitle());
            ps.setInt(4, order.getCount());
            ps.setInt(5, order.getOrderPrice());
            ps.setString(6, order.getBookimage());
            String status = (order.getDeliveryStatus() != null) ? order.getDeliveryStatus() : "배송준비중";
            ps.setString(7, status);
            
            return ps.executeUpdate();
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return 0; 
        }
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
                    .memberId(rs.getString("member_id"))
                    .bookId(rs.getInt("book_id"))
                    .title(rs.getString("title"))
                    .count(rs.getInt("count"))
                    .orderPrice(rs.getInt("order_price"))
                    .bookimage(rs.getString("bookimage"))
                    .orderDate(rs.getString("order_date"))
                    .deliveryStatus(rs.getString("delivery_status")) //배송정보 추가
                    .build());
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
    
    /* =================================================================
     * 🆕 [관리자 기능] 전체 유저의 주문 내역 조회 (최신순)
     * ================================================================= */
    public List<OrderVO> findAllOrdersForAdmin() {
        List<OrderVO> list = new ArrayList<>();
        String sql = "SELECT * FROM ORDERS ORDER BY order_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(OrderVO.builder()
                    .orderId(rs.getInt("order_id"))
                    .memberId(rs.getString("member_id")) // 관리자용이므로 주문자 ID 포함
                    .title(rs.getString("title"))
                    .count(rs.getInt("count"))
                    .orderPrice(rs.getInt("order_price"))
                    .bookimage(rs.getString("bookimage"))
                    .orderDate(rs.getString("order_date"))
                    .deliveryStatus(rs.getString("delivery_status"))
                    .build());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    

    /* =================================================================
     * 🆕 [관리자 기능] 특정 주문의 배송 상태 변경 처리
     * ================================================================= */
    public int updateDeliveryStatus(int orderId, String status) {
        String sql = "UPDATE ORDERS SET delivery_status = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; }
    }
}