package book.model;

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
public class ReviewVO {
    private int reviewId;    // 리뷰 고유 번호
    private int bookId;      // 대상 도서 번호
    private String memberId; // 작성자 ID
    private String content;  // 리뷰 내용
    private int rating;      // 평점 (1~5)
    private String regDate;  // 작성 일자
}