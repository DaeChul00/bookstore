package order.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("orderReviewDao")
public class ReviewDAOH2 {
    
    @Autowired
    private Connection conn;

    public int insertReview(int bookId, String memberId, int rating, String content) {
        String sql = "INSERT INTO REVIEW (BOOK_ID, MEMBER_ID, RATING, CONTENT, REG_DATE) "
                   + "VALUES (?, ?, ?, ?, NOW())";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setString(2, memberId);
            ps.setInt(3, rating);
            ps.setString(4, content);
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}