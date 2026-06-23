package book.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import book.model.BookVO;
import book.model.ReviewVO;

@Repository
public class BookDAOH2 implements BookDAO{
	@Autowired
	Connection conn;
	
	@Override
	public int insert(BookVO book) {
		String sql="INSERT INTO BOOK (isbn, title, author, publisher, publictiondate, price, content, bookimage, rating) "
	            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	      try {
	         PreparedStatement ps = conn.prepareStatement(sql);
	           ps.setString(1, book.getIsbn());
	           ps.setString(2, book.getTitle());
	           ps.setString(3, book.getAuthor());
	           ps.setString(4, book.getPublisher());
	           ps.setString(5, book.getPublictiondate());
	           ps.setInt(6, book.getPrice());
	           ps.setString(7, book.getContent());
	           ps.setString(8, book.getBookimage());
	           ps.setFloat(9, book.getRating());
	         
	         int result=ps.executeUpdate();
	         ps.close();
	         return result;
	      } catch (SQLException e) {
	         e.printStackTrace();
	         return 0;
	      }   
	}

	@Override
	public List<BookVO> findAll() {
		List<BookVO> list = new ArrayList<>();
        String sql = "SELECT * FROM BOOK ORDER BY ID DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
            	list.add(resultSetToBook(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
	}

	@Override
	public BookVO findById(int id) {
BookVO book = null;
		
		// 🎯 [서브쿼리 방식으로 변경] b.*의 모든 컬럼을 안전하게 가져오면서 평점과 개수를 정확하게 집계합니다.
		String sql = "SELECT b.*, " +
		             "       (SELECT COALESCE(AVG(r.RATING), 0.0) FROM REVIEW r WHERE r.BOOK_ID = b.ID) AS AVG_RATING, " +
		             "       (SELECT COUNT(r.REVIEW_ID) FROM REVIEW r WHERE r.BOOK_ID = b.ID) AS REVIEW_COUNT " +
		             "FROM BOOK b " +
		             "WHERE b.ID = ?";
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					book = BookVO.builder()
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
						
						// 🚨 대소문자가 정확히 맞는지 다시 한번 확인합니다.
						.avgRating(Math.round(rs.getDouble("AVG_RATING") * 10) / 10.0)
						.reviewCount(rs.getInt("REVIEW_COUNT"))
						
						.build();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return book;
	}

	@Override
	public int update(BookVO book) {
		String sql = "UPDATE BOOK SET " +
	            "ISBN=?, TITLE=?, AUTHOR=?, PUBLISHER=?, "+
	            "PUBLICTIONDATE=?, PRICE=?, CONTENT=?, "+
	            "BOOKIMAGE=?, RATING=? "+
	            "WHERE ID=?";
		      try(PreparedStatement ps = conn.prepareStatement(sql)) {
		         ps.setString(1, book.getIsbn());
		         ps.setString(2, book.getTitle());
		         ps.setString(3, book.getAuthor());
		         ps.setString(4, book.getPublisher());
		         ps.setString(5, book.getPublictiondate());
		         ps.setInt(6, book.getPrice());
		         ps.setString(7, book.getContent());
		         ps.setString(8, book.getBookimage());
		         ps.setFloat(9, book.getRating());
		         ps.setInt(10, book.getId());
		         
		         return ps.executeUpdate();
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
		      
		      return 0;
	}
	private BookVO resultSetToBook(ResultSet rs){

    	try {
    		BookVO book = new BookVO();

    		book.setId(rs.getInt("ID"));
            book.setIsbn(rs.getString("ISBN"));
            book.setTitle(rs.getString("TITLE"));
            book.setAuthor(rs.getString("AUTHOR"));
            book.setPublisher(rs.getString("PUBLISHER"));
            book.setPublictiondate(rs.getString("PUBLICTIONDATE"));
            book.setPrice(rs.getInt("PRICE"));
            book.setContent(rs.getString("CONTENT"));
            book.setBookimage(rs.getString("BOOKIMAGE"));
            book.setRating(rs.getFloat("RATING"));
            
        return book;
    	}catch (Exception e) {
    		e.printStackTrace();
    		return null;
		}
    }
	
	@Override
	public int delete(int id) {
	    String sql = "DELETE FROM BOOK WHERE ID = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, id);
	        return ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return 0;
	    }
	}
	
	@Override
	public List<BookVO> findAll(String category, String keyword) {
	    List<BookVO> list = new ArrayList<>();

	    //1. category 먼저 검증 (항상 실행)
	    if (!"title".equals(category) &&
	        !"author".equals(category) &&
	        !"publisher".equals(category)) {
	        category = "title";
	    }

	    StringBuilder sql = new StringBuilder("SELECT * FROM BOOK");

	    //2. keyword 있을 때만 WHERE 추가
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        sql.append(" WHERE ").append(category).append(" LIKE ?");
	    }

	    sql.append(" ORDER BY ID DESC");

	    try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {

	        //3. 파라미터 세팅
	        if (keyword != null && !keyword.trim().isEmpty()) {
	            ps.setString(1, "%" + keyword + "%");
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(resultSetToBook(rs));
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	@Override
	public List<BookVO> findTopRatedBooks() {
	    List<BookVO> list = new ArrayList<>();
	    String sql = "SELECT * FROM BOOK ORDER BY RATING DESC LIMIT 8";

	    try (PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            list.add(resultSetToBook(rs));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	@Override
	public List<BookVO> findNewBooks() {
	    List<BookVO> list = new ArrayList<>();
	    
	    // 최신순 (id 기준)
	    String sql = "SELECT * FROM BOOK ORDER BY ID DESC LIMIT 8";

	    try (PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            list.add(resultSetToBook(rs));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	// 🎯 1. 메인화면/추천목록용 실시간 평점 리스트 조회
	@Override
    public List<BookVO> getBestBooks() {
        List<BookVO> list = new ArrayList<>();
        String sql = "SELECT b.*, " +
                     "       COALESCE(AVG(r.RATING), 0.0) AS AVG_RATING, " +
                     "       COUNT(r.REVIEW_ID) AS REVIEW_COUNT " +
                     "FROM BOOK b " +
                     "LEFT JOIN REVIEW r ON b.ID = r.BOOK_ID " +
                     "GROUP BY b.ID, b.TITLE, b.BOOKIMAGE, b.PRICE, b.AUTHOR " +
                     "ORDER BY AVG_RATING DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(BookVO.builder()
                    .id(rs.getInt("ID"))
                    .title(rs.getString("TITLE"))
                    .author(rs.getString("AUTHOR"))
                    .price(rs.getInt("PRICE"))
                    .bookimage(rs.getString("BOOKIMAGE"))
                    .avgRating(Math.round(rs.getDouble("AVG_RATING") * 10) / 10.0)
                    .reviewCount(rs.getInt("REVIEW_COUNT"))
                    .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
	// 🎯 2. 상세페이지용 실시간 평점 포함 도서 단건 조회
	@Override
    public BookVO getBook(int id) {
        BookVO book = null;
        String sql = "SELECT b.*, " +
                     "       COALESCE(AVG(r.RATING), 0.0) AS AVG_RATING, " +
                     "       COUNT(r.REVIEW_ID) AS REVIEW_COUNT " +
                     "FROM BOOK b " +
                     "LEFT JOIN REVIEW r ON b.ID = r.BOOK_ID " +
                     "WHERE b.ID = ? " +
                     "GROUP BY b.ID, b.TITLE, b.AUTHOR, b.PUBLISHER, b.PUBLICTIONDATE, b.PRICE, b.CONTENT, b.BOOKIMAGE, b.RATING";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book = BookVO.builder()
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
                        .avgRating(Math.round(rs.getDouble("AVG_RATING") * 10) / 10.0)
                        .reviewCount(rs.getInt("REVIEW_COUNT"))
                        .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

	// 🎯 3. 상세페이지용 해당 도서의 전체 리뷰 리스트 최신순 조회
    @Override
    public List<ReviewVO> getReviewsByBookId(int bookId) {
        List<ReviewVO> list = new ArrayList<>();
        String sql = "SELECT * FROM REVIEW WHERE BOOK_ID = ? ORDER BY REVIEW_ID DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(ReviewVO.builder()
                        .reviewId(rs.getInt("REVIEW_ID"))
                        .bookId(rs.getInt("BOOK_ID"))
                        .memberId(rs.getString("MEMBER_ID"))
                        .rating(rs.getInt("RATING"))
                        .content(rs.getString("CONTENT"))
                        .regDate(rs.getString("REG_DATE"))
                        .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public boolean hasAlreadyReviewed(int bookId, String memberId) {
        String sql = "SELECT COUNT(*) FROM REVIEW WHERE BOOK_ID = ? AND MEMBER_ID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setString(2, memberId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 카운트가 0보다 크면 이미 리뷰를 작성한 것입니다.
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
	
}