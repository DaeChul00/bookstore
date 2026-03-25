package member.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberVO {
	private String memberId; // DB의 MEMBER_ID와 매칭
    private String password; // 비밀번호
    private String name;     // 이름
    private String email;    // 이메일
    private String role;     // 권한 (USER / ADMIN)
    private Timestamp regdate; // 가입일
}
