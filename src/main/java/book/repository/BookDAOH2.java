package book.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import book.model.BookVO;

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
        String sql = "SELECT * FROM BOOK WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BookMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int update(BookVO book) {
        String sql = "UPDATE BOOK SET ISBN=?, TITLE=?, AUTHOR=?, PUBLISHER=?, PUBLICTIONDATE=?, PRICE=?, CONTENT=?, BOOKIMAGE=?, RATING=? WHERE ID=?";
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
        StringBuilder sql = new StringBuilder("SELECT * FROM BOOK");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" WHERE ").append(validCategory).append(" LIKE ?");
            return jdbcTemplate.query(sql.append(" ORDER BY ID DESC").toString(), new BookMapper(), "%" + keyword + "%");
        }
        return jdbcTemplate.query(sql.append(" ORDER BY ID DESC").toString(), new BookMapper());
    }

    @Override
    public List<BookVO> findTopRatedBooks() {
        String sql = "SELECT * FROM BOOK ORDER BY RATING DESC LIMIT 8";
        return jdbcTemplate.query(sql, new BookMapper());
    }

    @Override
    public List<BookVO> findNewBooks() {
        String sql = "SELECT * FROM BOOK ORDER BY ID DESC LIMIT 8";
        return jdbcTemplate.query(sql, new BookMapper());
    }

    // LIMIT, OFFSET 페이징 처리의 JdbcTemplate 컨버전
    @Override
    public List<BookVO> findWithPaging(String category, String keyword, int pagePerCount, int requestPage) {
        String validCategory = validateCategory(category);
        StringBuilder sql = new StringBuilder("SELECT * FROM BOOK");
        int offset = (requestPage - 1) * pagePerCount;

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" WHERE ").append(validCategory).append(" LIKE ? ORDER BY ID DESC LIMIT ? OFFSET ?");
            return jdbcTemplate.query(sql.toString(), new BookMapper(), "%" + keyword + "%", pagePerCount, offset);
        }
        sql.append(" ORDER BY ID DESC LIMIT ? OFFSET ?");
        return jdbcTemplate.query(sql.toString(), new BookMapper(), pagePerCount, offset);
    }

    // 총 도서 수 카운팅
    @Override
    public int getTotalCount(String category, String keyword) {
        String validCategory = validateCategory(category);
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM BOOK");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" WHERE ").append(validCategory).append(" LIKE ?");
            return jdbcTemplate.queryForObject(sql.toString(), Integer.class, "%" + keyword + "%");
        }
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
    }

    private String validateCategory(String category) {
        if (!"title".equals(category) && !"author".equals(category) && !"publisher".equals(category)) {
            return "title";
        }
        return category;
    }

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
                    .rating(rs.getFloat("RATING"))
                    .build();
        }
    }
}