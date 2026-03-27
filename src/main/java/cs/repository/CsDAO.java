package cs.repository;

import java.util.List;

import cs.model.CsVO;

public interface CsDAO {

	public int save(CsVO cv);
	public List <CsVO> findAll();
	public CsVO findById(int id);
	public int update(CsVO cv);
	public int delete(int id);
}
