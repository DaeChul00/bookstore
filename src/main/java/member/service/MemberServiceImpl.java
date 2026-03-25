package member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import member.model.MemberVO;
import member.repository.MemberDAO;

@Service // 이 클래스가 비즈니스 로직을 수행하는 서비스임을 Spring에 알림
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO; // 실제 DB 작업을 할 DAO를 호출함

    @Override
    public MemberVO login(String id, String pw) {
        // 단순 호출이지만, 나중에 로그인 실패 횟수 체크 등의 로직이 추가될 수 있음
        return memberDAO.login(id, pw);
    }

    @Override
    public void signup(MemberVO vo) {
        // 1. 아이디로 기존 회원이 있는지 조회 (findById 등 기존 메서드 활용)
        MemberVO existing = memberDAO.findById(vo.getMemberId());
        
        if (existing != null) {
            // 이미 아이디가 존재한다면 예외를 던지거나 처리를 중단해야 함
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        
        // 2. 존재하지 않을 때만 가입 진행
        memberDAO.signup(vo);
    }
    
    @Override
	public void updateMember(MemberVO vo) {
		memberDAO.updateMember(vo);
	}
    
    @Override
    public void withdraw(String memberId) {
        memberDAO.deleteMember(memberId);
    }

    @Override
    public List<MemberVO> getAllMembers(String sort) {
        return memberDAO.findAll(sort);
    }
	
	@Override
	public void changeRole(String memberId, String role) {
	    memberDAO.updateRole(memberId, role);
	}

}