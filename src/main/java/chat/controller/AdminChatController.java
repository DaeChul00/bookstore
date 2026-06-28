package chat.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import chat.service.ChatService;

@Controller
@RequestMapping("/admin")
public class AdminChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/roomList")
    public String roomList(Model model, Principal principal) {

        // 로그인 체크
        if (principal == null) {
            return "redirect:/login";
        }

        String loginId = principal.getName();

        // 관리자 권한 체크
        if (!"admin".equals(loginId)) {
            return "redirect:/access-denied";
        }

        // 데이터
        model.addAttribute("roomList", chatService.getRoomList());

        // ⭐ 핵심: layout 사용
        model.addAttribute("contentPage", "/WEB-INF/views/admin/roomList.jsp");

        return "layout/layout";
    }
}