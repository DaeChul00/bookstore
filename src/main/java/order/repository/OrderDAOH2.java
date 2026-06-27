package order.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import order.model.OrderVO;

@Repository
public class OrderDAOH2 {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// 1. 회원용 주문 저장 (토스 코드 + 주소록 데이터 1:1 결합 인서트)
	public int insertOrder(OrderVO order) {
		String sql = "INSERT INTO ORDERS (member_id, book_id, title, count, order_price, bookimage, delivery_status, order_code, zipcode, road_address) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String status = (order.getDeliveryStatus() != null) ? order.getDeliveryStatus() : "결제완료";

		return jdbcTemplate.update(sql, 
				order.getMemberId(), 
				order.getBookId(), 
				order.getTitle(), 
				order.getCount(),
				order.getOrderPrice(), 
				order.getBookimage(), 
				status,
				order.getOrderCode(),
				order.getZipcode(),
				order.getRoadAddress()
		);
	}

	// 2. 일반 유저용 마이페이지 주문 리스트 조회
	public List<OrderVO> findOrdersByMemberId(String memberId) {
		String sql = "SELECT ORDER_ID, BOOK_ID, TITLE, BOOKIMAGE, COUNT, ORDER_PRICE, ORDER_DATE, DELIVERY_STATUS "
				+ "FROM ORDERS WHERE MEMBER_ID = ? ORDER BY ORDER_ID DESC";
		try {
			return jdbcTemplate.query(sql, (rs, rowNum) -> OrderVO.builder().orderId(rs.getInt("ORDER_ID"))
					.bookId(rs.getInt("BOOK_ID")).title(rs.getString("TITLE")).bookimage(rs.getString("BOOKIMAGE"))
					.count(rs.getInt("COUNT")).orderPrice(rs.getInt("ORDER_PRICE"))
					.orderDate(rs.getTimestamp("ORDER_DATE") != null ? rs.getTimestamp("ORDER_DATE").toString() : null)
					.deliveryStatus(rs.getString("DELIVERY_STATUS")).build(), memberId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	// 3. 비회원 전용 주문 내역 리스트 조회
	public List<OrderVO> findNonMemberOrders(int orderId) {
		String sql = "SELECT ORDER_ID, BOOK_ID, TITLE, BOOKIMAGE, COUNT, ORDER_PRICE, ORDER_DATE, DELIVERY_STATUS, ZIPCODE, ROAD_ADDRESS "
				   + "FROM ORDERS WHERE ORDER_ID = ? ORDER BY ORDER_ID DESC";
		try {
			return jdbcTemplate.query(sql, (rs, rowNum) -> OrderVO.builder()
					.orderId(rs.getInt("ORDER_ID"))
					.bookId(rs.getInt("BOOK_ID"))
					.title(rs.getString("TITLE"))
					.bookimage(rs.getString("BOOKIMAGE"))
					.count(rs.getInt("COUNT"))
					.orderPrice(rs.getInt("ORDER_PRICE"))
					.orderDate(rs.getTimestamp("ORDER_DATE") != null ? rs.getTimestamp("ORDER_DATE").toString() : null)
					.deliveryStatus(rs.getString("DELIVERY_STATUS"))
					.zipcode(rs.getString("ZIPCODE"))
					.roadAddress(rs.getString("ROAD_ADDRESS"))
					.build(), orderId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>(); 
		}
	}

	// 4. 비회원 최종 주문 제출 시 데이터 저장
	public int nonlogInsert(OrderVO orderVO) {
	    String sql = "INSERT INTO ORDERS (BOOK_ID, TITLE, COUNT, ORDER_PRICE, BOOKIMAGE, ZIPCODE, ROAD_ADDRESS, DELIVERY_STATUS) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, '결제완료')";

	    KeyHolder keyHolder = new GeneratedKeyHolder();

	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        ps.setInt(1, orderVO.getBookId());
	        ps.setString(2, orderVO.getTitle());
	        ps.setInt(3, orderVO.getCount());
	        ps.setInt(4, orderVO.getOrderPrice());
	        ps.setString(5, orderVO.getBookimage());
	        ps.setString(6, orderVO.getZipcode());
	        ps.setString(7, orderVO.getRoadAddress());
	        return ps;
	    }, keyHolder);

	    if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("ORDER_ID")) {
	        return ((Number) keyHolder.getKeys().get("ORDER_ID")).intValue();
	    }
	    return 0;
	}

	// 5. 관리자 대시보드용 전체 내역 조회 API
	public List<OrderVO> findAllOrdersForAdmin() {
		String sql = "SELECT * FROM ORDERS ORDER BY order_date DESC";
		try {
			return jdbcTemplate.query(sql, (rs, rowNum) -> OrderVO.builder().orderId(rs.getInt("order_id"))
					.memberId(rs.getString("member_id")).bookId(rs.getInt("book_id")).title(rs.getString("title"))
					.count(rs.getInt("count")).orderPrice(rs.getInt("order_price")).bookimage(rs.getString("bookimage"))
					.orderDate(rs.getTimestamp("order_date") != null ? rs.getTimestamp("order_date").toString() : null)
					.deliveryStatus(rs.getString("delivery_status")).build());
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// 6. 관리자용 배송 상태 스위칭 변경 API (주문 아이디 기준)
	public int updateDeliveryStatus(int orderId, String status) {
		String sql = "UPDATE ORDERS SET delivery_status = ? WHERE order_id = ?";
		return jdbcTemplate.update(sql, status, orderId);
	}

	// 7. 관리자용 배송 상태 스위칭 변경 API (토스 주문 코드 기준)
	public int updateDeliveryStatusByCode(String orderCode, String status) {
		String sql = "UPDATE ORDERS SET delivery_status = ? WHERE order_code = ?";
		return jdbcTemplate.update(sql, status, orderCode);
	}

	// 8. 실시간 비동기 폴링을 위한 단건 배송 상태 텍스트 추출 쿼리
	public String getDeliveryStatus(int orderId) {
		String sql = "SELECT delivery_status FROM ORDERS WHERE order_id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, String.class, orderId);
		} catch (Exception e) {
			return "정보없음";
		}
	}

	// 9. 특정 단건 주문 상세 조회 바인딩용 쿼리
	public OrderVO getOrderById(int orderId) {
		String sql = "SELECT * FROM ORDERS WHERE order_id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> OrderVO.builder().orderId(rs.getInt("order_id"))
					.memberId(rs.getString("member_id")).bookId(rs.getInt("book_id")).title(rs.getString("title"))
					.count(rs.getInt("count")).orderPrice(rs.getInt("order_price")).bookimage(rs.getString("bookimage"))
					.orderDate(rs.getTimestamp("order_date") != null ? rs.getTimestamp("order_date").toString() : null)
					.deliveryStatus(rs.getString("delivery_status")).build(), orderId);
		} catch (Exception e) {
			return null;
		}
	}

	// 10. 주소록 갱신용 기본 주소 해제 처리
	public void disableDefaultAddress(String memberId) {
		String sql = "UPDATE MEMBER_ADDRESS SET IS_DEFAULT = 'N' WHERE MEMBER_ID = ? AND IS_DEFAULT = 'Y'";
		jdbcTemplate.update(sql, memberId);
	}
}