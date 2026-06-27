package order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    private int orderId;          // 주문 번호 (PK)
    private String orderCode;     // 토스 결제용 고유 코드 (ORDER_12345...)
    private String memberId;      // 구매자 아이디
    private int bookId;           // 구매한 책 번호
    private String title;         // 구매 당시 책 제목
    private int count;            // 구매 수량
    private int orderPrice;       // 구매 당시 가격 (단건 결제 총액)
    private String orderDate;     // 주문 날짜
    private String bookimage;     // 책 이미지
    private String deliveryStatus; // 배송 상태 필드
    private String zipcode;       // 배송 우편번호
    private String roadAddress;   // 배송 도로명 주소
}