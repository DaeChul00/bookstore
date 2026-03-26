package cs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.model.CsVO;
import cs.repository.CsDAO;

@Service
public class CsService {

	@Autowired
	private CsDAO dao;

	public List<CsVO> getCS() {
		return dao.findAll();
	}

	public boolean insert(CsVO cv) {
		int result = dao.save(cv);
		if (result > 0)
			return true;
		else
			return false;
	}

	public CsVO getCs(int id) {
		return dao.findById(id);
	}

	public boolean update(CsVO cv) {
		return dao.update(cv) > 0 ? true : false;
	}

	public boolean delete(int id) {
		return dao.delete(id) > 0 ? true : false;
	}
}
