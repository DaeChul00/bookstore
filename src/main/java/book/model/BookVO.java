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
public class BookVO {
	private int id;//데이터베이스 번호
	private String isbn;//책 식별번호
	private String title;//책 제목
	private String author;//책 저자
	private String publisher;//출판사
	private String publictiondate;//출판일
	private int price;//가격
	private String content;//내용
	private String bookimage;//메인 이미지
	private float rating;//평점
}
