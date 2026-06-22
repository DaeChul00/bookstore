package book.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import book.model.BookVO;
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
    
    // 💡 조원 컨트롤러 호출 명칭 스펙 일치
    public List<BookVO> getBooksWithPaging(String category, String keyword, int pagePerCount, int requestPage) {
        return dao.findWithPaging(category, keyword, pagePerCount, requestPage);
    }

    // 💡 총 도서 수 계산 서비스 매핑
    public int getTotalCount(String category, String keyword) {
        return dao.getTotalCount(category, keyword);
    }
}