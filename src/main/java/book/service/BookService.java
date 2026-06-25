package book.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import book.model.BookVO;
import book.model.ReviewVO;
import book.repository.BookDAO;

@Service
public class BookService {
	@Autowired
	private BookDAO dao;
	
	public List<BookVO> getBooks(){
		return dao.findAll();
	}
	public BookVO getBook(int id) {
		return dao.findById(id);
	}
	public boolean insert(BookVO book) {
		int result = dao.insert(book);
		return result > 0;
	}
	public boolean updateBook(BookVO book) {
		int result = dao.update(book);
		return result > 0;
	}
	public boolean delete(int id) {
	    int result = dao.delete(id);
	    return result > 0;
	}
	public List<BookVO> getBooks(String category, String keyword) {
	    return dao.findAll(category, keyword);
	}
	public List<BookVO> getTopRatedBooks(){
	    return dao.findTopRatedBooks();
	}

	public List<BookVO> getNewBooks(){
	    return dao.findNewBooks();
	}
	public List<BookVO> getBestBooks() {
	    return dao.getBestBooks();
	}
	public List<ReviewVO> getReviewsByBookId(int bookId) {
	    return dao.getReviewsByBookId(bookId);
	}
	
	public boolean hasAlreadyReviewed(int bookId, String memberId) {
		return dao.hasAlreadyReviewed(bookId, memberId);
	}
	public List<BookVO> searchBooks(String category, String keyword) {
		// TODO Auto-generated method stub
		return dao.searchBooks(category, keyword);
	};

}