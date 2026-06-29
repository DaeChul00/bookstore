package chat.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import chat.service.ChatService;

@Controller
@RequestMapping("/admin")
public class AdminChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/roomList")
    public String roomList(Model model, Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/access-denied";
        }

        model.addAttribute("roomList", chatService.getRoomList());
        model.addAttribute("contentPage", "/WEB-INF/views/admin/roomList.jsp");

        return "layout/layout";
    }
    
    @GetMapping("/roomListAjax")
    public String roomListAjax(Model model){

        model.addAttribute("roomList", chatService.getRoomList());

        return "admin/roomListBody";
    }
}