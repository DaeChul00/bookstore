package order.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("orderReviewDao")
public class ReviewDAOH2 {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertReview(int bookId, String memberId, int rating, String content) {
        String sql = "INSERT INTO REVIEW (BOOK_ID, MEMBER_ID, RATING, CONTENT, REG_DATE) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try {
            return jdbcTemplate.update(sql, bookId, memberId, rating, content);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}