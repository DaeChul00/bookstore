package book.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
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
import book.service.BookService;
import member.model.MemberVO;

@Controller
@RequestMapping("/book")
public class BookController {
	
	@Autowired
	BookService service;
	
	// 1. 메인 페이지 (추천 도서 및 신규 도서 로직 통합)
	@RequestMapping("")
	public String main(Model model) {
	    // 서비스에서 평점 높은 도서와 신규 도서 리스트를 가져옵니다.
	    List<BookVO> topRatedList = service.getTopRatedBooks();
	    List<BookVO> newBookList = service.getNewBooks();

	    model.addAttribute("topRatedList", topRatedList);
	    model.addAttribute("newBookList", newBookList);

	    // 메인 페이지 전용 JSP로 연결합니다.
	    model.addAttribute("contentPage", "/WEB-INF/views/main.jsp");
	    return "layout/layout";
	}

	// 2. 도서 등록 폼 (관리자 권한 체크 로직 유지)
	@RequestMapping(value = "insertform", method = RequestMethod.GET)
	public String insertform(Model model, HttpSession session) {
	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
	        return "redirect:/book/list";
	    }

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

	// 3. 도서 목록 (카테고리/키워드 검색 기능 유지)
	@RequestMapping("list")
	public ModelAndView list(
	        @RequestParam(value = "category", required = false, defaultValue = "title") String category,
	        @RequestParam(value = "keyword", required = false) String keyword) {
	    
	    ModelAndView mv = new ModelAndView();
	    List<BookVO> list = service.getBooks(category, keyword);
	    
	    mv.addObject("list", list);
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

	// 5. 수정 및 삭제 (관리자 권한 체크 포함)
	@RequestMapping(value = "update", method = RequestMethod.GET)
	public ModelAndView updateform(int id, HttpSession session) {
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
	        return new ModelAndView("redirect:/book/list");
	    }

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
	public String delete(@RequestParam("id") int id, RedirectAttributes ra, HttpSession session) {
	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
	        return "redirect:/book/list";
	    }

	    ra.addFlashAttribute("kind", "delete");
	    if(service.delete(id)) {
	        ra.addFlashAttribute("message", "success");
	    } else {
	        ra.addFlashAttribute("message", "fail");
	    }
	    return "redirect:/book/list";
	}
}