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
    private int orderId;      // 주문 번호 (PK)
    private String memberId;  // 구매자 아이디
    private int bookId;       // 구매한 책 번호
    private String title;     // 구매 당시 책 제목
    private int count;        // 구매 수량
    private int orderPrice;   // 구매 당시 가격
    private String orderDate; // 주문 날짜
    private String bookimage; // 책 이미지
}