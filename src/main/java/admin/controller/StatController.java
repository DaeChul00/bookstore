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
		model.addAttribute("publisherStats", statService.getPublisherStats());
	    model.addAttribute("dailySales", statService.getDailySales());
	    model.addAttribute("weeklySales", statService.getWeeklySales()); // 추가
	    model.addAttribute("monthlySales", statService.getMonthlySales());
	    model.addAttribute("yearlySales", statService.getYearlySales());
	    model.addAttribute("publisherSales", statService.getPublisherSales());
	    model.addAttribute("bestSellers", statService.getBestSellers());
	    model.addAttribute("topBooks", statService.getTopRatedBooks());
	    
	    model.addAttribute("contentPage", "/WEB-INF/views/admin/sales-stats.jsp");
	    return "layout/layout";
    }
}
