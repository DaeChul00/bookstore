package book.repository;

import java.util.List;
import book.model.BookVO;

public interface BookDAO {
    public int insert(BookVO book);
    public List<BookVO> findAll();
    public BookVO findById(int id);
    public int update(BookVO book);
    public int delete(int id);
    public List<BookVO> findAll(String category, String keyword);
    public List<BookVO> findTopRatedBooks();
    public List<BookVO> findNewBooks();
    public List<BookVO> findWithPaging(String category, String keyword, int pagePerCount, int requestPage);
    public int getTotalCount(String category, String keyword);
}