package book.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import book.model.BookVO;
import book.model.ReviewVO;
import book.page.BookPage;
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
    
    public BookPage getBooksWithPaging(String category, String keyword, int pagePerCount, int requestPage) {
        int totalCount = dao.getTotalCount(category, keyword);
        int totalPage = (int) Math.ceil((double) totalCount / pagePerCount);
        if (totalPage == 0) totalPage = 1;
        
        if (requestPage > totalPage) requestPage = totalPage;
        if (requestPage < 1) requestPage = 1;
        
        int startPage = ((requestPage - 1) / 10) * 10 + 1;
        int endPage = startPage + 9;
        if (endPage > totalPage) endPage = totalPage;
        
        boolean pre = startPage > 1;
        boolean next = endPage < totalPage;
        
        List<BookVO> list = dao.findWithPaging(category, keyword, pagePerCount, requestPage);
        
        return new BookPage(pagePerCount, totalCount, totalPage, requestPage, startPage, endPage, pre, next, list);
    }
    
}