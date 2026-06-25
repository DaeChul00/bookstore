package admin.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
		String sql = "SELECT * FROM book ORDER BY rating DESC LIMIT 5";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BookVO book = new BookVO();
            book.setId(rs.getInt("id"));
            book.setTitle(rs.getString("title"));
            book.setRating(rs.getFloat("rating"));
            return book;
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
