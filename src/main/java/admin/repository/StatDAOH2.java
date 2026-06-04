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
		// 최근 7일간의 날짜별 총 매출액 합계
	    String sql = "SELECT FORMATDATETIME(order_date, 'yyyy-MM-dd') as date, SUM(order_price * count) as total_sales " +
	                 "FROM ORDERS GROUP BY date ORDER BY date DESC LIMIT 7";
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

	

}
