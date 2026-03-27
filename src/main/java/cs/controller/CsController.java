package cs.controller;

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

import cs.model.CsVO;
import cs.service.CsService;
import cs.model.CsInsertVO;

@Controller
@RequestMapping("/cs")
public class CsController {

    @Autowired
    private CsService service;

    private ModelAndView render(String pageName) {
        ModelAndView mv = new ModelAndView("layout/layout");
        // /WEB-INF/views/cs/파일명.jsp 구조로 고정 (컨트롤러 수준에서 관리)
        mv.addObject("contentPage", String.format("/WEB-INF/views/cs/%s.jsp", pageName));
        return mv;
    }

    @RequestMapping("")
    public String defaultPage() {
        return "redirect:/cs/csList";
    }

//    // 등록 폼
//    @RequestMapping("insertform")
//    public ModelAndView insertform() {
//        return render("insertform");
//    }
    
    @RequestMapping("insertform")
    public ModelAndView csWrite() {
        ModelAndView mv = render("csWrite");
        mv.addObject("csWrite", service.getCS());
        return mv;
    }

    // 등록 처리
    @RequestMapping("insert")
    public String insert(@ModelAttribute CsInsertVO ics, RedirectAttributes ra) {
        CsVO cv = new CsVO();
        BeanUtils.copyProperties(ics, cv);

        ra.addFlashAttribute("kind", "insert");
        boolean success = service.insert(cv);
        ra.addFlashAttribute("message", success ? "success" : "fail");

        return "redirect:/cs/csList";
    }

    // 목록 조회
    @RequestMapping("csList")
    public ModelAndView csList() {
        ModelAndView mv = render("csList");
        mv.addObject("csList", service.getCS());
        return mv;
    }

    // 상세 조회
    @RequestMapping("view")
    public ModelAndView view(@RequestParam("id") int id) {
        ModelAndView mv = render("view");
        mv.addObject("cv", service.getCs(id));
        return mv;
    }

    // 수정 폼
    @RequestMapping("updateform")
    public ModelAndView updateform(@RequestParam("id") int id) {
        ModelAndView mv = render("updateform");
        mv.addObject("cv", service.getCs(id));
        return mv;
    }

    // 수정 처리
    @RequestMapping("update")
    public String update(CsVO cv, RedirectAttributes ra) {
        ra.addFlashAttribute("kind", "update");
        boolean success = service.update(cv);
        ra.addFlashAttribute("message", success ? "success" : "fail");

        return "redirect:/cs/view?id=" + cv.getId();
    }

    // 삭제 처리
    @RequestMapping("delete")
    public String delete(@RequestParam("id") int id, RedirectAttributes ra) {
        ra.addFlashAttribute("kind", "delete");
        boolean success = service.delete(id);
        ra.addFlashAttribute("message", success ? "success" : "fail");
        
        return "redirect:/cs/csList";
    }
}