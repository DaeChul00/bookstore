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
		String sql = "SELECT * FROM BOOK WHERE ID = ?";
	       
	       try (PreparedStatement ps = conn.prepareStatement(sql)) {
	           
	           ps.setInt(1, id);
	           ResultSet rs = ps.executeQuery();
	           
	           if (rs.next()) {
	               BookVO book = new BookVO();

	               book.setId(rs.getInt("ID"));
	               book.setIsbn(rs.getString("ISBN"));
	               book.setTitle(rs.getString("TITLE"));
	               book.setAuthor(rs.getString("AUTHOR"));
	               book.setPublisher(rs.getString("PUBLISHER"));
	               book.setPublictiondate(rs.getString("PUBLICTIONDATE"));
	               book.setPrice(rs.getInt("PRICE"));
	               book.setBookimage(rs.getString("BOOKIMAGE"));
	               book.setContent(rs.getString("CONTENT"));
	               book.setRating(rs.getFloat("RATING"));
	                   
	               return book;
	           }
	           
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	       
	       return null;
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
	
	
}
