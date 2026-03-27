package admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import admin.service.StatService;

@Controller
@RequestMapping("/admin")
public class StatController {
	@Autowired
	StatService statService;
	
	@GetMapping("/stat/sales")
    public String getSalesStats(Model model) {
        // 출판사별 통계 데이터
        model.addAttribute("publisherStats", statService.getPublisherStats());
        
        // 일별 판매량
        model.addAttribute("dailySales", statService.getDailySales());
        
        // 베스트셀러
        model.addAttribute("bestSellers", statService.getBestSellers());
        
        // 평점 TOP 5 도서 데이터
        model.addAttribute("topBooks", statService.getTopRatedBooks());
        
        model.addAttribute("contentPage", "/WEB-INF/views/admin/sales-stats.jsp");
        
        // WEB-INF/views/admin/sales-stats.jsp로 이동
        return "layout/layout";
    }
}
