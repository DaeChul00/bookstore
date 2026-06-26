package admin.repository;

import java.util.List;
import java.util.Map;
import book.model.BookVO;

public interface StatDAO {
	List<Map<String, Object>> getPublisherCount();
    List<Map<String, Object>> getDailySales();    // 일별 매출
    List<Map<String, Object>> getMonthlySales();
    List<Map<String, Object>> getYearlySales();
    List<Map<String, Object>> getPublisherSales();
    List<Map<String, Object>> getBestSellers();
    List<BookVO> getTopRatedBooks();
	List<Map<String, Object>> getWeeklySales();
    
}
