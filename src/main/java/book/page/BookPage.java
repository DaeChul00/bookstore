package book.page;

import java.util.List;

import book.model.BookVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BookPage {
	int pagePerCount;
	int totalCount;
	int totalPage;
	int requestPage;
	int startPage;
	int endPage;
	boolean pre;
	boolean next;
	List<BookVO> list;
}
