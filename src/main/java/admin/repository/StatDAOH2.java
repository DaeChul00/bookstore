package admin.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import book.model.BookVO;

@Repository
public class StatDAOH2 implements StatDAO{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getPublisherCount() {
		String sql = "SELECT publisher, COUNT(*) as count FROM book GROUP BY publisher";
        return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getDailySales() {
	    String sql =
	        "SELECT FORMATDATETIME(ORDER_DATE, 'yyyy-MM-dd') AS SALE_DATE, " +
	        "SUM(ORDER_PRICE) AS TOTAL_SALES " +
	        "FROM ORDERS " +
	        "GROUP BY FORMATDATETIME(ORDER_DATE, 'yyyy-MM-dd') " +
	        "ORDER BY SALE_DATE DESC LIMIT 7";

	    return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getBestSellers() {
		// 판매 수량(count) 기준 TOP 5 도서
	    String sql = "SELECT title, SUM(count) as total_count FROM ORDERS " +
	                 "GROUP BY title ORDER BY total_count DESC LIMIT 5";
	    return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<BookVO> getTopRatedBooks() {
	    // REVIEW 테이블의 별점을 실시간 평균 내어(AVG_RATING) 정렬하도록 수정
	    String sql = "SELECT B.*, " +
	                 "COALESCE((SELECT AVG(RATING) FROM REVIEW WHERE BOOK_ID = B.ID), 0.0) AS AVG_RATING, " +
	                 "COALESCE((SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = B.ID), 0) AS REVIEW_COUNT " +
	                 "FROM BOOK B ORDER BY AVG_RATING DESC LIMIT 5";
	                 
	    // 만약 StatDAOH2 내부에 BookMapperWithReview가 없다면, 
	    // 기존에 작성했던 BookDAOH2에 있는 BookMapperWithReview 클래스를 가져오거나 
	    // 아래 매퍼를 StatDAOH2 파일 하단에 추가해서 사용해야 합니다.
	    return jdbcTemplate.query(sql, new RowMapper<BookVO>() {
	        @Override
	        public BookVO mapRow(ResultSet rs, int rowNum) throws SQLException {
	            double avgRating = rs.getDouble("AVG_RATING");
	            return BookVO.builder()
	                    .id(rs.getInt("ID"))
	                    .isbn(rs.getString("ISBN"))
	                    .title(rs.getString("TITLE"))
	                    .author(rs.getString("AUTHOR"))
	                    .publisher(rs.getString("PUBLISHER"))
	                    .publictiondate(rs.getString("PUBLICTIONDATE"))
	                    .price(rs.getInt("PRICE"))
	                    .content(rs.getString("CONTENT"))
	                    .bookimage(rs.getString("BOOKIMAGE"))
	                    .rating((float) avgRating) // 옛날 변수명 호환용
	                    .avgRating(avgRating)
	                    .reviewCount(rs.getInt("REVIEW_COUNT"))
	                    .build();
	        }
	    });
	}
	@Override
	public List<Map<String, Object>> getMonthlySales() {
	    String sql =
	        "SELECT FORMATDATETIME(ORDER_DATE, 'yyyy-MM') AS SALE_MONTH, " +
	        "SUM(ORDER_PRICE) AS TOTAL_SALES " +
	        "FROM ORDERS " +
	        "GROUP BY FORMATDATETIME(ORDER_DATE, 'yyyy-MM') " +
	        "ORDER BY SALE_MONTH DESC";

	    return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getYearlySales() {
	    String sql =
	        "SELECT FORMATDATETIME(ORDER_DATE, 'yyyy') AS SALE_YEAR, " +
	        "SUM(ORDER_PRICE) AS TOTAL_SALES " +
	        "FROM ORDERS " +
	        "GROUP BY FORMATDATETIME(ORDER_DATE, 'yyyy') " +
	        "ORDER BY SALE_YEAR DESC";

	    return jdbcTemplate.queryForList(sql);
	}

    @Override
    public List<Map<String, Object>> getPublisherSales() {
        // BOOK 테이블과 JOIN하여 출판사별 매출 합계 계산
        String sql = "SELECT B.PUBLISHER, SUM(O.ORDER_PRICE) as TOTAL_SALES " +
                     "FROM ORDERS O JOIN BOOK B ON O.BOOK_ID = B.ID " +
                     "GROUP BY B.PUBLISHER";
        return jdbcTemplate.queryForList(sql);
    }

   
    @Override
    public List<Map<String, Object>> getWeeklySales() {
        String sql =
            "SELECT FORMATDATETIME(ORDER_DATE, 'yyyy-w') AS SALE_WEEK, " +
            "SUM(ORDER_PRICE) AS TOTAL_SALES " +
            "FROM ORDERS " +
            "GROUP BY FORMATDATETIME(ORDER_DATE, 'yyyy-w') " +
            "ORDER BY SALE_WEEK DESC";

        return jdbcTemplate.queryForList(sql);
    }
	

	

}
