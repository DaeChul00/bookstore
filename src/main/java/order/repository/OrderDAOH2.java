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

	// 1. 주문 저장 (배송 상태 기본값 '주문완료' 안전 처리)
	public int insertOrder(OrderVO order) {
		String sql = "INSERT INTO ORDERS (member_id, book_id, title, count, order_price, bookimage, delivery_status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		String status = (order.getDeliveryStatus() != null) ? order.getDeliveryStatus() : "주문완료";

		return jdbcTemplate.update(sql, order.getMemberId(), order.getBookId(), order.getTitle(), order.getCount(),
				order.getOrderPrice(), order.getBookimage(), status);
	}

	// 2. 일반 유저용 마이페이지 주문 리스트 조회 (대철님 그룹화 정렬 쿼리 고수)
	public List<OrderVO> findOrdersByMemberId(String memberId) {
		String sql = "SELECT ORDER_ID, BOOK_ID, TITLE, BOOKIMAGE, COUNT, ORDER_PRICE, ORDER_DATE, DELIVERY_STATUS "
				+ "FROM ORDERS " + "WHERE MEMBER_ID = ? " + "ORDER BY ORDER_ID DESC";
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
	
	// 1. 비회원 전용 주문 내역 리스트 조회
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

		// 2. 비회원 최종 주문 제출 시 데이터 저장
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

	// 3. 관리자 대시보드용 전체 내역 조회 API
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

	// 4. 관리자용 배송 상태 스위칭 변경 API
	public int updateDeliveryStatus(int orderId, String status) {
		String sql = "UPDATE ORDERS SET delivery_status = ? WHERE order_id = ?";
		return jdbcTemplate.update(sql, status, orderId);
	}

	// 실시간 비동기 폴링을 위한 단건 배송 상태 텍스트 추출 쿼리
	public String getDeliveryStatus(int orderId) {
		String sql = "SELECT delivery_status FROM ORDERS WHERE order_id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, String.class, orderId);
		} catch (Exception e) {
			return "정보없음";
		}
	}

	// 특정 단건 주문 상세 조회 바인딩용 쿼리
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

	public void disableDefaultAddress(String memberId) {
		String sql = "UPDATE MEMBER_ADDRESS SET IS_DEFAULT = 'N' WHERE MEMBER_ID = ? AND IS_DEFAULT = 'Y'";
		jdbcTemplate.update(sql, memberId);

	}

	public void insertAddress(OrderVO orderVO) {
	    // 1. ⭕ 책 관련 컬럼(BOOK_ID, TITLE, COUNT, ORDER_PRICE, BOOKIMAGE)과 물음표(?)를 추가합니다.
	    String sql = "INSERT INTO ORDERS (MEMBER_ID, BOOK_ID, TITLE, COUNT, ORDER_PRICE, BOOKIMAGE, ZIPCODE, ROAD_ADDRESS, DELIVERY_STATUS) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '결제완료')";

	    // 2. ⭕ 상단 쿼리의 ? 순서와 1:1로 정확하게 매칭되도록 orderVO의 Getter들을 순서대로 배치합니다.
	    jdbcTemplate.update(sql, 
	        orderVO.getMemberId(),     // 1번째 ? (로그인 안 했으면 null 전달됨)
	        orderVO.getBookId(),       // 2번째 ?
	        orderVO.getTitle(),        // 3번째 ?
	        orderVO.getCount(),        // 4번째 ?
	        orderVO.getOrderPrice(),   // 5번째 ? (단가 * 수량이 계산된 최종 금액)
	        orderVO.getBookimage(),    // 6번째 ?
	        orderVO.getZipcode(),      // 7번째 ?
	        orderVO.getRoadAddress()   // 8번째 ? (JSP에서 기본주소+상세주소가 합쳐진 문자열)
	    );
	}
}