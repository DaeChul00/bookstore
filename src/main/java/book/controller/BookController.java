package book.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.model.BookVO;
import book.model.ReviewVO;
import book.service.BookService;

@Controller
@RequestMapping("/book")
public class BookController {
	
	@Autowired
	private BookService service;
	
	// 스프링 시큐리티 관제탑에서 현재 로그인한 유저의 ROLE_ADMIN 권한 여부를 체크하는 공통 메서드
	private boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return false;
		}
		return auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}
	
	// 1. 메인 페이지 (추천 도서 및 신규 도서 로직 통합)
	@RequestMapping("")
	public String main(Model model) {
	    List<BookVO> topRatedList = service.getBestBooks();
	    List<BookVO> newBookList = service.getNewBooks();

	    model.addAttribute("topRatedList", topRatedList);
	    model.addAttribute("newBookList", newBookList);
	    model.addAttribute("contentPage", "/WEB-INF/views/main.jsp");
	    
	    // 메인 페이지에서만 배너 표시
	    model.addAttribute("showBanner", true);
	    
	    return "layout/layout";
	}

	// 2. 도서 등록 폼
	@RequestMapping(value = "insertform", method = RequestMethod.GET)
	public String insertform(Model model) {
		if (!isAdmin()) {
			return "redirect:/book/list";
		}
		model.addAttribute("contentPage", "/WEB-INF/views/book/insertform.jsp");
		return "layout/layout";
	}

	// 3. 도서 목록 및 검색 (페이징 연산 통합)
	@RequestMapping("list")
	public ModelAndView list(@RequestParam(value = "category", required = false, defaultValue = "title") String category,
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {

		ModelAndView mv = new ModelAndView();
		int pagePerCount = 6; 
		
		book.page.BookPage p = service.getBooksWithPaging(category, keyword, pagePerCount, page);
		
		mv.addObject("p", p);
		mv.addObject("category", category);
		mv.addObject("keyword", keyword);
		
		mv.addObject("contentPage", "/WEB-INF/views/book/list.jsp");
		mv.setViewName("layout/layout");
		return mv;
	}

	// 4. 상세 보기 (조원분이 구현한 실시간 리뷰 리스트 바인딩 이식)
	@RequestMapping("view")
	public ModelAndView view(int id) {
		ModelAndView mv = new ModelAndView();
		
		// 도서 정보 조회
		BookVO book = service.getBook(id);
		// 해당 도서에 달린 리뷰 리스트 가져오기
		List<ReviewVO> reviewList = service.getReviewsByBookId(id);
		
		mv.addObject("bk", book);
		mv.addObject("reviewList", reviewList);
		mv.addObject("contentPage", "/WEB-INF/views/book/view.jsp");
		mv.setViewName("layout/layout");
		return mv;
	}

	// 5. 수정 및 삭제 (시큐리티 권한 체크 반영 완료)
	@RequestMapping(value = "update", method = RequestMethod.GET)
	public ModelAndView updateform(int id) {
	    if (!isAdmin()) {
	        return new ModelAndView("redirect:/book/list");
	    }

	    ModelAndView mv = new ModelAndView();
	    mv.addObject("bk", service.getBook(id));
		mv.addObject("contentPage", "/WEB-INF/views/book/updateform.jsp");
		mv.setViewName("layout/layout");
		return mv;
	}

	@RequestMapping(value = {"update", "insert"}, method = RequestMethod.POST)
	public String update(BookVO bk, RedirectAttributes ra) {
		if (!isAdmin()) {
			return "redirect:/book/list";
		}

		if (bk.getId() == 0) {
			ra.addFlashAttribute("kind", "insert");
			
			if (service.insert(bk)) { 
				ra.addFlashAttribute("message", "success");
			} else {
				ra.addFlashAttribute("message", "fail");
			}
			return "redirect:/admin/book/list";
		}

		ra.addFlashAttribute("kind", "update");
		if(service.updateBook(bk)) {
			ra.addFlashAttribute("message", "success");
		} else {
			ra.addFlashAttribute("message", "fail");
		}
		return "redirect:/admin/book/list";
	}
	
	@RequestMapping("delete")
	public String delete(@RequestParam("id") int id, RedirectAttributes ra) {
	    if (!isAdmin()) {
	        return "redirect:/book/list";
	    }

	    ra.addFlashAttribute("kind", "delete");
	    if (service.delete(id)) {
	        ra.addFlashAttribute("message", "success");
	    } else {
	        ra.addFlashAttribute("message", "fail");
	    }
	    return "redirect:/admin/book/list"; 
	}
	
	// ISBN 기반 카카오 외부 API 자동완성 인프라 보존
	@ResponseBody
	@RequestMapping(value = "/fetchBookInfo", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public BookVO fetchBookInfo(@RequestParam("isbn") String isbn) {
		if (isbn == null || isbn.trim().isEmpty()) {
			return null;
		}

		try {
			String url = "https://dapi.kakao.com/v3/search/book?target=isbn&query=" + isbn.trim();
			String apiKey = "KakaoAK a02091f188b98e20cf7cb974f679a4be"; 
			
			org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
			org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
			headers.set("Authorization", apiKey);
			
			org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
			
			org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
				url, 
				org.springframework.http.HttpMethod.GET, 
				entity, 
				String.class
			);
			
			com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
			com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.getBody());
			com.fasterxml.jackson.databind.JsonNode documents = root.path("documents");
			
			if (documents.isArray() && documents.size() > 0) {
				com.fasterxml.jackson.databind.JsonNode bookNode = documents.get(0);
				
				StringBuilder authors = new StringBuilder();
				if (bookNode.path("authors").isArray()) {
					for (com.fasterxml.jackson.databind.JsonNode author : bookNode.path("authors")) {
						if (authors.length() > 0) authors.append(", ");
						authors.append(author.asText());
					}
				}
				
				String rawDate = bookNode.path("datetime").asText();
				String formattedDate = "";
				if (rawDate != null && rawDate.length() >= 10) {
					formattedDate = rawDate.substring(0, 4) + "년 " + rawDate.substring(5, 7) + "월 " + rawDate.substring(8, 10) + "일";
				}
				
				return BookVO.builder()
						.isbn(isbn.trim())
						.title(bookNode.path("title").asText())
						.author(authors.toString())
						.publisher(bookNode.path("publisher").asText())
						.publictiondate(formattedDate)
						.price(bookNode.path("price").asInt())
						.content(bookNode.path("contents").asText())
						.bookimage(bookNode.path("thumbnail").asText())
						.rating(0.0f)
						.build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 메인화면 실시간 검색창 처리용 Ajax 엔드포인트 완벽 흡수
	@RequestMapping("search")
	@ResponseBody
	public List<BookVO> search(
	        @RequestParam String category,
	        @RequestParam String keyword){
	    return service.searchBooks(category, keyword);
	}
}