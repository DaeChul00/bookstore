package member.service;

import java.util.List;

import member.model.MemberVO;

public interface MemberService {

	MemberVO login(String memberId, String password);
	void signup(MemberVO vo);
	List<MemberVO> getAllMembers();
	void changeRole(String memberId, String role);
}
