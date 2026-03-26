package member.repository;

import java.util.List;

import member.model.MemberVO;


public interface MemberDAO {
	
	MemberVO login(String memberId, String password);
	void signup(MemberVO vo);
	void updateMember(MemberVO vo);
	void deleteMember(String memberId);
	List<MemberVO> findAll(String sort);
	void updateRole(String memberId, String role);
	MemberVO findById(String memberId);
}
