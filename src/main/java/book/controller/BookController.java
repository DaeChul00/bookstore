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
	
	@RequestMapping("/")
	public String defaultPage() {
	    return "redirect:/book/list";
	}
	@RequestMapping(value = "insert",method = RequestMethod.GET)
	public String insertform(Model model, HttpSession session) { // HttpSession 추가
	    // 세션에서 로그인 정보 가져오기 (MemberVO 클래스명은 본인의 프로젝트에 맞게 수정)
	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    // 관리자가 아니면 리스트로 튕겨내기
	    if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
	        return "redirect:/book/list";
	    }

	    String folder ="book";
	    String page="insertform";
	    String contentPage=String.format("/WEB-INF/views/%s/%s.jsp",folder,page);
	    model.addAttribute("contentPage",contentPage);
	    return "layout/layout";
	}
	
	@RequestMapping(value = "insert",method = RequestMethod.POST)
	public  String insert(@ModelAttribute BookVO ibk, RedirectAttributes ra) {
		
		BookVO bk = new BookVO();
		BeanUtils.copyProperties(ibk, bk);
		
		ra.addFlashAttribute("kind","insert");
		if(service.insert(bk)) {
			ra.addFlashAttribute("message","success");
		}else {
			ra.addFlashAttribute("message","fail");
		}
		return "redirect:/book/list";
	}
	@RequestMapping("list")
	public ModelAndView list(
	        @RequestParam(value = "category", required = false, defaultValue = "title") String category,
	        @RequestParam(value = "keyword", required = false) String keyword) {
	    
	    ModelAndView mv = new ModelAndView();
	    // 검색 조건에 맞는 리스트 가져오기
	    List<BookVO> list = service.getBooks(category, keyword);
	    
	    mv.addObject("list", list);
	    mv.addObject("contentPage", "/WEB-INF/views/book/list.jsp");
	    mv.setViewName("layout/layout");
	    return mv;
	}
	@RequestMapping("view")
	public ModelAndView view(int id, HttpServletRequest request) {
		ModelAndView mv =new ModelAndView();
		mv.addObject("bk", service.getBook(id));
		
		String[] paths=request.getRequestURI().split("/");
		String contentPage=String.format("/WEB-INF/views/%s/%s.jsp", paths[1],paths[2]);
		
		mv.addObject("contentPage",contentPage);
		
		
		mv.setViewName("layout/layout");
		return mv;
	}
	@RequestMapping(value = "update",method = RequestMethod.GET)
	public ModelAndView updateform(int id, HttpServletRequest request, HttpSession session) {
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
	        return new ModelAndView("redirect:/book/list");
	    }

	    ModelAndView mv = new ModelAndView();
	    mv.addObject("bk", service.getBook(id));
		
	    String folder ="book";
	    String page="updateform";
	    String contentPage=String.format("/WEB-INF/views/%s/%s.jsp",folder,page);
		
		mv.addObject("contentPage",contentPage);
		
		mv.setViewName("layout/layout");
		return mv;
	}
	@RequestMapping(value = "update",method = RequestMethod.POST)
	public String update(BookVO bk, RedirectAttributes ra) {
		ra.addFlashAttribute("kind","update");
		System.out.println(bk);
		if(service.updateBook(bk)) {
			ra.addFlashAttribute("message","success");
		}else {
			ra.addFlashAttribute("message","fail");
		}
		return "redirect:/book/view?id="+bk.getId();
	}
	
	@RequestMapping("delete")
	public String delete(@RequestParam("id") int id, RedirectAttributes ra, HttpSession session) {
	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    // 관리자가 아니면 삭제 불가 
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