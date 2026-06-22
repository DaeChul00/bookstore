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
	private int id;//�����ͺ��̽� ��ȣ
	private String isbn;//å �ĺ���ȣ
	private String title;//å ����
	private String author;//å ����
	private String publisher;//���ǻ�
	private String publictiondate;//������
	private int price;//����
	private String content;//����
	private String bookimage;//���� �̹���
	private float rating;//����
	private double avgRating;   // 실시간 평점 평균 (예: 4.5)
	private int reviewCount;    // 실시간 총 리뷰 수
}