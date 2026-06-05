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

@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	@Qualifier("memberDAOH2")
	private MemberDAO memberDAO; 

	// 시큐리티 인증용 비밀번호 암호화 컴포넌트 주입
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public MemberVO login(String id, String pw) {
		return memberDAO.login(id, pw);
	}

	@Override
	public void signup(MemberVO vo) {
		// 1. 입력받은 아이디로 기존 회원이 존재하는지 검증
		MemberVO existing = memberDAO.findById(vo.getMemberId());

		if (existing != null) {
			// 깨진 한글 구문을 깔끔하게 정리 완료
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		}

		vo.setPassword(passwordEncoder.encode(vo.getPassword()));
		// 3. 기본 권한 등급을 USER로 고정합니다.
		vo.setRole("USER");

		// 4. 안전하게 암호화된 상태로 DB에 삽입 처리합니다.
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
	
	/**
	 * 💡 [스프링 시큐리티 로그인 연동 핵심 메서드]
	 * 사용자가 로그인을 시도하면 시큐리티 관제탑이 이 메서드를 호출하여 
	 * DB의 암호화된 정보와 사용자가 입력한 비밀번호를 자동으로 대조 및 인증해 줍니다.
	 */
	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

		System.out.println("Spring Security 로그인 인증 시도 ID : " + memberId);

		// DB에서 회원 정보 조회
		MemberVO member = memberDAO.findById(memberId);

		if (member == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}

		// 시큐리티 규격에 맞는 User 객체를 생성하여 권한(ROLE_USER 등)과 함께 반환합니다.
		return User.builder()
				.username(member.getMemberId())
				.password(member.getPassword()) // DB에 저장된 암호화된 비밀번호
				.authorities(new SimpleGrantedAuthority("ROLE_" + member.getRole()))
				.build();
	}
}