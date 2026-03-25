package book.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.model.BookVO;
import book.service.BookService;

@Controller
@RequestMapping("/book")
public class BookController {
	
	@Autowired
	BookService service;
	
	@RequestMapping("")
	public String defaultPage() {
	    return "redirect:/book/list";
	}
	@RequestMapping("insertform")
	public String insertform(Model model) {
		String folder ="book";
		String page="insertform";
		String contentPage=String.format("/WEB-INF/views/%s/%s.jsp",folder,page);
		model.addAttribute("contentPage",contentPage);
		return "layout/layout";
	}
	
	@RequestMapping("insert")
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
	public ModelAndView list(HttpServletRequest request) {
		System.out.println(">>> 컨트롤러 접속 성공! <<<");
		ModelAndView mv =new ModelAndView();
		mv.addObject("list", service.getBooks());
		String[] paths=request.getRequestURI().split("/");
		String contentPage=String.format("/WEB-INF/views/%s/%s.jsp", paths[1],paths[2]);
		mv.addObject("contentPage",contentPage);
		
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
	@RequestMapping("updateform")
	public ModelAndView updateform(int id,HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("bk", service.getBook(id));
		
		String[] paths=request.getRequestURI().split("/");
		String contentPage=String.format("/WEB-INF/views/%s/%s.jsp", paths[1],paths[2]);
		
		mv.addObject("contentPage",contentPage);
		
		
		mv.setViewName("layout/layout");
		return mv;
	}
	@RequestMapping("update")
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
//	@RequestMapping("delete")
//	public String delete(int id) {
//		
//		service.delete(id);
//		return "redirect:/book/list";
//	}

}
