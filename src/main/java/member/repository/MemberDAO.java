package member.repository;

import java.util.List;

import member.model.MemberVO;


public interface MemberDAO {
	
	MemberVO login(String memberId, String password);
	void signup(MemberVO vo);
	public List<MemberVO> findAll();
	void updateRole(String memberId, String role);
	MemberVO findById(String memberId);
}
