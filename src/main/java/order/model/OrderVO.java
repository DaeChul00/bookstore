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
    private int orderId;          // 기존 PK
    private String orderCode;     // 토스 결제용 고유 코드
    private String memberId;
    private int bookId;
    private String title;
    private int count;
    private int orderPrice;
    private String orderDate;
    private String bookimage;
    private String deliveryStatus;
}