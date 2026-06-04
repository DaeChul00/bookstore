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
    private String deliveryStatus; // 배송 상태
    
    /*
    H2 DB 새로 생성 (이렇게 하는게 오류가 없었음)
    CREATE TABLE ORDERS (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(50),
    book_id INT,
    title VARCHAR(200),
    count INT,
    order_price INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    bookimage VARCHAR(500),
    delivery_status VARCHAR(50) DEFAULT '배송준비중'
	);
    */
}