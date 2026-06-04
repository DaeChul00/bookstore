package admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.repository.StatDAO;
import book.model.BookVO;

@Service
public class StatServiceImpl implements StatService{
	
	@Autowired
	private StatDAO statDAO;
	
	@Override
	public List<Map<String, Object>> getPublisherStats() {
		return statDAO.getPublisherCount();
	}

	@Override
	public List<Map<String, Object>> getDailySales() {
		return statDAO.getDailySales();
	}

	@Override
	public List<Map<String, Object>> getBestSellers() {
		return statDAO.getBestSellers();
	}
	
	@Override
	public List<BookVO> getTopRatedBooks() {
		return statDAO.getTopRatedBooks();
	}

}
