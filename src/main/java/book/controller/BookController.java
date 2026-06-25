package book.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.model.BookVO;
import book.page.BookPage;
import book.service.BookService;

@Controller
@RequestMapping("/book")
public class BookController {
	
	@Autowired
	private BookService service;
	
	// 1. 메인 페이지 (추천 도서 및 신규 도서 로직 통합)
	@RequestMapping("")
	public String main(Model model) {
	    model.addAttribute("topRatedList", service.getTopRatedBooks());
	    model.addAttribute("newBookList", service.getNewBooks());
	    model.addAttribute("contentPage", "/WEB-INF/views/main.jsp");
	    
	    // 메인 페이지에서만 배너 표시
	    model.addAttribute("showBanner", true);
	    
	    return "layout/layout";
	}

	// 2. 도서 등록 폼 (시큐리티 자동 필터 체인 보호 이용)
	@RequestMapping(value = "insertform", method = RequestMethod.GET)
	public String insertform(Model model) {
	    model.addAttribute("contentPage", "/WEB-INF/views/book/insertform.jsp");
	    return "layout/layout";
	}
	
	@RequestMapping(value = "insert", method = RequestMethod.POST)
	public String insert(@ModelAttribute BookVO ibk, RedirectAttributes ra) {
		ra.addFlashAttribute("kind", "insert");
		if(service.insert(ibk)) {
			ra.addFlashAttribute("message", "success");
		} else {
			ra.addFlashAttribute("message", "fail");
		}
		return "redirect:/book/list";
	}

	// 3. 도서 목록 (전달한 페이징 연산 공식 정밀 통합)
	@RequestMapping("list")
	public ModelAndView list(
	        @RequestParam(value = "category", required = false, defaultValue = "title") String category,
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
	    
	    ModelAndView mv = new ModelAndView();
	    
	    int pagePerCount = 6;  
	    int displayPageNum = 10; 

	    // 1. 전체 게시글 수 구하기
	    int totalCount = service.getTotalCount(category, keyword);
	    
	    // 2. 전체 페이지 수 구하기
	    int totalPage = (int) Math.ceil((double) totalCount / pagePerCount);
	    if (totalPage == 0) totalPage = 1;
	    
	    // 안전장치
	    if (page > totalPage) page = totalPage;
	    if (page < 1) page = 1;

	    // 3. 10페이지 단위의 시작 페이지와 끝 페이지 계산
	    int endPage = (int) (Math.ceil(page / (double) displayPageNum) * displayPageNum);
	    int startPage = (endPage - displayPageNum) + 1;
	    
	    if (totalPage < endPage) {
	        endPage = totalPage;
	    }

	    // 4. 이전(pre), 다음(next) 계산
	    boolean pre = startPage > 1;
	    boolean next = endPage < totalPage;

	    // 5. 현재 페이지에 해당하는 데이터만 가져오기
	    List<BookVO> list = service.getBooksWithPaging(category, keyword, pagePerCount, page);

	    // 6. BookPage 객체 생성 및 세팅
	    BookPage bookPage = new BookPage();
	    bookPage.setPagePerCount(pagePerCount);
	    bookPage.setTotalCount(totalCount);
	    bookPage.setTotalPage(totalPage);
	    bookPage.setRequestPage(page);
	    bookPage.setStartPage(startPage);
	    bookPage.setEndPage(endPage);
	    bookPage.setPre(pre);
	    bookPage.setNext(next);
	    bookPage.setList(list);

	    mv.addObject("p", bookPage); 
	    mv.addObject("list", list); 
	    mv.addObject("category", category);
	    mv.addObject("keyword", keyword);
	    
	    mv.addObject("contentPage", "/WEB-INF/views/book/list.jsp");
	    mv.setViewName("layout/layout");
	    return mv;
	}

	// 4. 상세 보기
	@RequestMapping("view")
	public ModelAndView view(int id) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("bk", service.getBook(id));
		mv.addObject("contentPage", "/WEB-INF/views/book/view.jsp");
		mv.setViewName("layout/layout");
		return mv;
	}

	// 5. 수정 및 삭제
	@RequestMapping(value = "updateform", method = RequestMethod.GET)
	public ModelAndView updateform(int id) {
	    ModelAndView mv = new ModelAndView();
	    mv.addObject("bk", service.getBook(id));
		mv.addObject("contentPage", "/WEB-INF/views/book/updateform.jsp");
		mv.setViewName("layout/layout");
		return mv;
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(BookVO bk, RedirectAttributes ra) {
		ra.addFlashAttribute("kind", "update");
		if(service.updateBook(bk)) {
			ra.addFlashAttribute("message", "success");
		} else {
			ra.addFlashAttribute("message", "fail");
		}
		return "redirect:/book/view?id=" + bk.getId();
	}
	
	@RequestMapping("delete")
	public String delete(@RequestParam("id") int id, RedirectAttributes ra) {
	    ra.addFlashAttribute("kind", "delete");
	    if(service.delete(id)) {
	        ra.addFlashAttribute("message", "success");
	    } else {
	        ra.addFlashAttribute("message", "fail");
	    }
	    return "redirect:/book/list";
	}
}