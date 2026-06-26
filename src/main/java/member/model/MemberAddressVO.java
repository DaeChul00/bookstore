package member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberAddressVO {
    
    private int addrNo;          // 주소 고유 번호 (ADDR_NO)
    private String memberId;     // 회원 아이디 (MEMBER_ID)
    private String addrName;     // 배송지 별칭 (ADDR_NAME)
    private String zipcode;      // 우편번호 (ZIPCODE)
    private String roadAddress;  // 도로명 주소 + 상세주소 (ROAD_ADDRESS)
    private String isDefault;    // 기본 배송지 여부 'Y' 또는 'N' (IS_DEFAULT)
    
}