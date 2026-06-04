package order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CartVO {
    private int cartId;      // 장바구니 고유 번호 (PK, AUTO_INCREMENT)
    private String memberId; // 사용자 아이디 (누구의 장바구니인가)
    private int bookId;      // 도서 번호 (BookVO의 id와 매칭)
    private int count;       // 담은 수량
    
    // 선택사항: 화면에 책 제목이나 가격을 바로 보여주기 위해 추가할 수 있습니다.
    private String title;    // 도서 제목
    private int price;       // 도서 가격
    private String bookimage;// 도서 이미지
}