package book.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import book.model.BookVO;
import book.repository.BookDAO;
import book.repository.BookDAOH2;

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

}
