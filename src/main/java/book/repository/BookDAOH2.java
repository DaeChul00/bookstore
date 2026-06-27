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
        // ⚙️ 수정: VALUES에서 rating 제거
        String sql = "INSERT INTO BOOK (isbn, title, author, publisher, publictiondate, price, content, bookimage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(),
                book.getPublictiondate(), book.getPrice(), book.getContent(), book.getBookimage());
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
        // ⚙️ 수정: SET 구문에서 rating=? 제거
        String sql = "UPDATE BOOK SET isbn=?, title=?, author=?, publisher=?, publictiondate=?, price=?, content=?, bookimage=? WHERE id=?";
        return jdbcTemplate.update(sql, book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(),
                book.getPublictiondate(), book.getPrice(), book.getContent(), book.getBookimage(), book.getId());
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
        // ⚙️ 수정: 테이블에 RATING이 없으므로 REVIEW 평점 평균(AVG_RATING)을 계산해 상위 5개 정렬
        String sql = "SELECT B.*, " +
                     "COALESCE((SELECT AVG(RATING) FROM REVIEW WHERE BOOK_ID = B.ID), 0.0) AS AVG_RATING, " +
                     "COALESCE((SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = B.ID), 0) AS REVIEW_COUNT " +
                     "FROM BOOK B ORDER BY AVG_RATING DESC LIMIT 5";
        return jdbcTemplate.query(sql, new BookMapperWithReview());
    }

    @Override
    public List<BookVO> findNewBooks() {
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
        // ⚙️ 수정: 터졌던 에러의 주원인 구문 해결 (B.RATING 대신 계산된 서브쿼리 결과인 AVG_RATING DESC로 정렬)
        String sql = "SELECT B.*, " +
                     "COALESCE((SELECT AVG(RATING) FROM REVIEW WHERE BOOK_ID = B.ID), 0.0) AS AVG_RATING, " +
                     "COALESCE((SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = B.ID), 0) AS REVIEW_COUNT " +
                     "FROM BOOK B ORDER BY AVG_RATING DESC LIMIT 5";
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

    // 기본 매퍼 (조인 없는 기본 도서 조회용)
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
                    .content(rs.getString("CONTENT"))
                    .bookimage(rs.getString("BOOKIMAGE"))
                    // ⚙️ 수정: DB에 더이상 RATING 컬럼이 없으므로 rs.getFloat("RATING") 제거하고 기본값 0.0f 할당
                    .rating(0.0f) 
                    .build();
        }
    }

    // 리뷰 조인 매퍼 (별점 및 리뷰 개수 포함 조회용)
    private static final class BookMapperWithReview implements RowMapper<BookVO> {
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
                    // ⚙️ 수정: 오리지널 rating 변수 자리에도 연산된 평균 평점(avgRating)을 넣어주어 프론트 깨짐 방지
                    .rating((float) avgRating) 
                    .avgRating(avgRating)
                    .reviewCount(rs.getInt("REVIEW_COUNT"))
                    .build();
        }
    }

    @Override
    public List<BookVO> searchBooks(String category, String keyword) {
    	String validCategory = validateCategory(category);

        String sql =
            "SELECT * " +
            "FROM BOOK " +
            "WHERE " + validCategory + " LIKE ? " +
            "ORDER BY " +
            "CASE " +
            "   WHEN " + validCategory + " LIKE ? THEN 0 " +
            "   WHEN " + validCategory + " LIKE ? THEN 1 " +
            "   ELSE 2 " +
            "END, " +
            validCategory + " ASC " +
            "LIMIT 6";

        return jdbcTemplate.query(
                sql,
                new BookMapper(),
                "%" + keyword + "%",  // 검색
                keyword + "%",        // 시작 일치
                "%" + keyword + "%"   // 포함 일치
        );
    }
}