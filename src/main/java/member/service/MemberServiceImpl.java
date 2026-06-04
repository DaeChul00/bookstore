package member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import member.model.MemberVO;
import member.repository.MemberDAO;

@Service // �� Ŭ������ ����Ͻ� ������ �����ϴ� �������� Spring�� �˸�
public class MemberServiceImpl implements MemberService {
	
    @Autowired
    @Qualifier("memberDAOH2")
    private MemberDAO memberDAO; // ���� DB �۾��� �� DAO�� ȣ����

    @Override
    public MemberVO login(String id, String pw) {
        // �ܼ� ȣ��������, ���߿� �α��� ���� Ƚ�� üũ ���� ������ �߰��� �� ����
        return memberDAO.login(id, pw);
    }

	/*
	 * @Override public void signup(MemberVO vo) { // 1. ���̵�� ���� ȸ���� �ִ��� ��ȸ
	 * (findById �� ���� �޼��� Ȱ��) MemberVO existing =
	 * memberDAO.findById(vo.getMemberId());
	 * 
	 * if (existing != null) { // �̹� ���̵� �����Ѵٸ� ���ܸ� �����ų� ó���� �ߴ��ؾ� ��
	 * throw new RuntimeException("�̹� �����ϴ� ���̵��Դϴ�."); }
	 * 
	 * // 2. �������� ���� ���� ���� ���� memberDAO.signup(vo); }
	 */
    @Override
    public void signup(MemberVO vo) {

        MemberVO existing =
                memberDAO.findById(
                        vo.getMemberId());

        if (existing != null) {

            throw new RuntimeException(
                    "이미 존재하는 아이디입니다.");
        }

        vo.setPassword(
                passwordEncoder.encode(
                        vo.getPassword()));

        vo.setRole("USER");

        memberDAO.signup(vo);
    }
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
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
	
	public UserDetails loadUserByUsername(String memberId){

	    System.out.println("Spring Security 로그인 시도 : "
	            + memberId);

	    MemberVO member =
	            memberDAO.findById(memberId);

	    if (member == null) {

	        throw new UsernameNotFoundException(
	                "사용자를 찾을 수 없습니다.");
	    }

	    return User.builder()
	            .username(member.getMemberId())
	            .password(member.getPassword())
	            .authorities(
	                    new SimpleGrantedAuthority(
	                            "ROLE_" + member.getRole()))
	            .build();
	}

}