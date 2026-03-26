package admin.repository;

import java.util.List;
import java.util.Map;
import book.model.BookVO;

public interface StatDAO {
	// 출판사별 도서 수 통계
	List<Map<String,Object>> getPublisherCount();

    // 날짜별 매출액
    List<Map<String, Object>> getDailySales();
    
    //베스트 셀러
    List<Map<String, Object>> getBestSellers();
    
	// 평점 높은 도서 TOP 5
    List<BookVO> getTopRatedBooks();
    
}
