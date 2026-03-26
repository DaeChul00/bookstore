package admin.service;

import java.util.List;
import java.util.Map;

import book.model.BookVO;

public interface StatService {
	List<Map<String, Object>> getPublisherStats();
	List<Map<String, Object>> getDailySales();
	List<Map<String, Object>> getBestSellers();
    List<BookVO> getTopRatedBooks();
    
}
