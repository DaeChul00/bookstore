package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import member.model.MemberVO;
import member.repository.MemberDAO;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    @Autowired
    private MemberDAO memberDAO;

    @Override
    public UserDetails loadUserByUsername(
            String memberId)
            throws UsernameNotFoundException {

        MemberVO member =
                memberDAO.selectById(memberId);

        if(member == null) {
            throw new UsernameNotFoundException(
                    memberId);
        }

        return User.builder()
                .username(member.getMemberId())
                .password(member.getPassword())
                // .roles(member.getRole()) // 이 줄을 지우고 아래 줄로 교체
                .authorities("ROLE_" + member.getRole()) // 명시적으로 접두사를 1번만 붙임
                .build();
    }
}