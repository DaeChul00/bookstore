package cs.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

        if (authentication != null) {
            cv.setUserName(authentication.getName());
        }
        
        cv.setStatus("답변대기"); 

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
    public ModelAndView view(@RequestParam("id") int id, Authentication authentication) {
        ModelAndView mv = render("view");
        CsVO vo = service.getCs(id);
        
        mv.addObject("cv", vo);
        
        boolean isAdmin = false;
        if (authentication != null) {
            isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                   || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER_ADMIN"))
                   || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_BOOK_ADMIN"));
        }
        mv.addObject("isAdmin", isAdmin);
        
        return mv;
    }

    @RequestMapping(value = "answer", method = RequestMethod.POST)
    public String answer(@RequestParam("id") int id, 
                         @RequestParam("answer") String answer, 
                         RedirectAttributes ra,
                         Authentication authentication) {
        
        CsVO cv = service.getCs(id);
        if (cv != null) {
            cv.setAnswer(answer);
            cv.setStatus("답변완료"); // 답변이 등록되면 상태를 전환합니다.
            if (authentication != null) {
                cv.setAdminId(authentication.getName());
            }
            service.update(cv); // 데이터 업데이트 수행
            ra.addFlashAttribute("message", "success");
        }
        return "redirect:/cs/view?id=" + id;
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