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
                .roles(member.getRole())
                .build();
    }
}