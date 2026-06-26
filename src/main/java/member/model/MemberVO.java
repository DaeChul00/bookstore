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
	private String memberId; // DB�� MEMBER_ID�� ��Ī
    private String password; // ��й�ȣ
    private String name;     // �̸�
    private String email;    // �̸���
    private String zipcode;
    private String roadAddress;
    private String role;     // ���� (USER / ADMIN)
    private Timestamp regdate; // ������
}
