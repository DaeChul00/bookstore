package book.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import book.model.BookVO;
import book.model.ReviewVO;

@Repository
public class BookDAOH2 implements BookDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insert(BookVO book) {
        String sql = "INSERT INTO BOOK (isbn, title, author, publisher, publictiondate, price, content, bookimage, rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(),
                book.getPublictiondate(), book.getPrice(), book.getContent(), book.getBookimage(), book.getRating());
    }

    @Override
    public List<BookVO> findAll() {
        String sql = "SELECT * FROM BOOK ORDER BY ID DESC";
        return jdbcTemplate.query(sql, new BookMapper());
    }

    @Override
    public BookVO findById(int id) {
        String sql = "SELECT B.*, " +
                     "COALESCE((SELECT AVG(RATING) FROM REVIEW WHERE BOOK_ID = B.ID), 0.0) AS AVG_RATING, " +
                     "COALESCE((SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = B.ID), 0) AS REVIEW_COUNT " +
                     "FROM BOOK B WHERE B.ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BookMapperWithReview(), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int update(BookVO book) {
        String sql = "UPDATE BOOK SET isbn=?, title=?, author=?, publisher=?, publictiondate=?, price=?, content=?, bookimage=?, rating=? WHERE id=?";
        return jdbcTemplate.update(sql, book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(),
                book.getPublictiondate(), book.getPrice(), book.getContent(), book.getBookimage(), book.getRating(), book.getId());
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM BOOK WHERE ID = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<BookVO> findAll(String category, String keyword) {
        String validCategory = validateCategory(category);
        String sql = "SELECT * FROM BOOK WHERE " + validCategory + " LIKE ? ORDER BY ID DESC";
        return jdbcTemplate.query(sql, new BookMapper(), "%" + keyword + "%");
    }

    @Override
    public List<BookVO> findTopRatedBooks() {
        String sql = "SELECT * FROM BOOK ORDER BY RATING DESC LIMIT 5";
        return jdbcTemplate.query(sql, new BookMapper());
    }

    @Override
    public List<BookVO> findNewBooks() {
        // ⭕ 신간 도서 조회 시 데이터 크래시 방지를 위해 Review 매퍼 가동
        String sql = "SELECT B.*, " +
                     "COALESCE((SELECT AVG(RATING) FROM REVIEW WHERE BOOK_ID = B.ID), 0.0) AS AVG_RATING, " +
                     "COALESCE((SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = B.ID), 0) AS REVIEW_COUNT " +
                     "FROM BOOK B ORDER BY B.PUBLICTIONDATE DESC LIMIT 5";
        return jdbcTemplate.query(sql, new BookMapperWithReview());
    }

    @Override
    public List<BookVO> findWithPaging(String category, String keyword, int pagePerCount, int requestPage) {
        String validCategory = validateCategory(category);
        int offset = (requestPage - 1) * pagePerCount;
        
        String sql = "SELECT B.*, " +
                     "COALESCE((SELECT AVG(RATING) FROM REVIEW WHERE BOOK_ID = B.ID), 0.0) AS AVG_RATING, " +
                     "COALESCE((SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = B.ID), 0) AS REVIEW_COUNT " +
                     "FROM BOOK B " +
                     "WHERE B." + validCategory + " LIKE ? " +
                     "ORDER BY B.ID DESC LIMIT ? OFFSET ?";
        
        return jdbcTemplate.query(sql, new BookMapperWithReview(), "%" + keyword + "%", pagePerCount, offset);
    }

    @Override
    public int getTotalCount(String category, String keyword) {
        String validCategory = validateCategory(category);
        String sql = "SELECT COUNT(*) FROM BOOK WHERE " + validCategory + " LIKE ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, "%" + keyword + "%");
    }

    @Override
    public List<BookVO> getBestBooks() {
        String sql = "SELECT B.*, " +
                     "COALESCE((SELECT AVG(RATING) FROM REVIEW WHERE BOOK_ID = B.ID), 0.0) AS AVG_RATING, " +
                     "COALESCE((SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = B.ID), 0) AS REVIEW_COUNT " +
                     "FROM BOOK B ORDER BY B.RATING DESC LIMIT 5";
        return jdbcTemplate.query(sql, new BookMapperWithReview());
    }

    @Override
    public BookVO getBook(int id) {
        return this.findById(id);
    }

    @Override
    public List<ReviewVO> getReviewsByBookId(int bookId) {
        String sql = "SELECT * FROM REVIEW WHERE BOOK_ID = ? ORDER BY REVIEW_ID DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> ReviewVO.builder()
                .reviewId(rs.getInt("REVIEW_ID"))
                .bookId(rs.getInt("BOOK_ID"))
                .memberId(rs.getString("MEMBER_ID"))
                .rating(rs.getInt("RATING"))
                .content(rs.getString("CONTENT"))
                .regDate(rs.getTimestamp("REG_DATE") != null ? rs.getTimestamp("REG_DATE").toString() : "")
                .build(), bookId);
    }

    @Override
    public boolean hasAlreadyReviewed(int bookId, String memberId) {
        String sql = "SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = ? AND MEMBER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookId, memberId);
        return count != null && count > 0;
    }

    private String validateCategory(String category) {
        if (!"title".equals(category) && !"author".equals(category) && !"publisher".equals(category)) {
            return "title";
        }
        return category;
    }

    // 기본 매퍼 데이터 타입 정상화 완료
    private static final class BookMapper implements RowMapper<BookVO> {
        @Override
        public BookVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return BookVO.builder()
                    .id(rs.getInt("ID"))
                    .isbn(rs.getString("ISBN"))
                    .title(rs.getString("TITLE"))
                    .author(rs.getString("AUTHOR"))
                    .publisher(rs.getString("PUBLISHER"))
                    .publictiondate(rs.getString("PUBLICTIONDATE"))
                    .price(rs.getInt("PRICE"))
                    .content(rs.getString("CONTENT")) // ⭕ 에러 완전 해결: 기존 rs.getInt 구조를 안전한 String으로 교정
                    .bookimage(rs.getString("BOOKIMAGE"))
                    .rating(rs.getFloat("RATING"))
                    .build();
        }
    }

    // 리뷰 조인 매퍼 데이터 타입 정상화 완료
    private static final class BookMapperWithReview implements RowMapper<BookVO> {
        @Override
        public BookVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return BookVO.builder()
                    .id(rs.getInt("ID"))
                    .isbn(rs.getString("ISBN"))
                    .title(rs.getString("TITLE"))
                    .author(rs.getString("AUTHOR"))
                    .publisher(rs.getString("PUBLISHER"))
                    .publictiondate(rs.getString("PUBLICTIONDATE"))
                    .price(rs.getInt("PRICE"))
                    .content(rs.getString("CONTENT")) // ⭕ 에러 완전 해결: 기존 rs.getInt 구조를 안전한 String으로 교정
                    .bookimage(rs.getString("BOOKIMAGE"))
                    .rating(rs.getFloat("RATING"))
                    .avgRating(rs.getDouble("AVG_RATING"))
                    .reviewCount(rs.getInt("REVIEW_COUNT"))
                    .build();
        }
    }
    
    
}