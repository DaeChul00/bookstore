package member.service;

import java.util.List;

import member.model.MemberVO;

public interface MemberService {

	MemberVO login(String memberId, String password);
	void signup(MemberVO vo);
	void updateMember(MemberVO vo);
	void withdraw(String memberId);
	List<MemberVO> getAllMembers(String sort);
	void changeRole(String memberId, String role);
}
