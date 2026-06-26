package cs.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

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
        mv.addObject("contentPage", String.format("/WEB-INF/views/cs/%s.jsp", pageName));
        return mv;
    }

    @RequestMapping("")
    public String defaultPage() {
        return "redirect:/cs/csList";
    }
    
    @RequestMapping("insertform")
    public ModelAndView csWrite() {
        ModelAndView mv = render("csWrite");
        mv.addObject("csWrite", service.getCS());
        return mv;
    }

    @RequestMapping("insert")
    public String insert(@ModelAttribute CsInsertVO ics,
                         RedirectAttributes ra,
                         Authentication authentication) {

        CsVO cv = new CsVO();
        BeanUtils.copyProperties(ics, cv);

        // 🔒 [시큐리티 완벽 연동]: 로그인한 사용자의 진짜 아이디 추출 및 새 변수 매핑
        if (authentication != null) {
            cv.setUserName(authentication.getName());
        }

        ra.addFlashAttribute("kind", "insert");
        boolean success = service.insert(cv);
        ra.addFlashAttribute("message", success ? "success" : "fail");

        return "redirect:/cs/csList";
    }

    @RequestMapping("csList")
    public ModelAndView csList() {
        ModelAndView mv = render("csList");
        mv.addObject("csList", service.getCS());
        return mv;
    }

    @RequestMapping("view")
    public ModelAndView view(@RequestParam("id") int id) {
        ModelAndView mv = render("view");
        mv.addObject("cv", service.getCs(id));
        return mv;
    }

    @RequestMapping("updateform")
    public ModelAndView updateform(@RequestParam("id") int id) {
        ModelAndView mv = render("updateform");
        mv.addObject("cv", service.getCs(id));
        return mv;
    }

    @RequestMapping("update")
    public String update(CsVO cv, RedirectAttributes ra) {
        ra.addFlashAttribute("kind", "update");
        boolean success = service.update(cv);
        ra.addFlashAttribute("message", success ? "success" : "fail");
        return "redirect:/cs/view?id=" + cv.getId();
    }

    @RequestMapping("delete")
    public String delete(@RequestParam("id") int id, RedirectAttributes ra) {
        ra.addFlashAttribute("kind", "delete");
        boolean success = service.delete(id);
        ra.addFlashAttribute("message", success ? "success" : "fail");
        return "redirect:/cs/csList";
    }
}