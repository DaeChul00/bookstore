package member.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import member.model.MemberVO;

public interface MemberService {

	MemberVO login(String memberId, String password);
	void signup(MemberVO vo);
	void updateMember(MemberVO vo);
	void withdraw(String memberId);
	List<MemberVO> getAllMembers(String sort);
	void changeRole(String memberId, String role);
	UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException;

}
