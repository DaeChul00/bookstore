package chat.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import chat.model.ChatMessageVO;
import chat.service.ChatService;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService service;

    @GetMapping("/room")
    public String room(
            @RequestParam Long roomId,
            Model model) {

        model.addAttribute("roomId", roomId);

        // 기존 채팅 내역 조회
        model.addAttribute(
                "messages",
                service.getMessages(roomId));

        return "chat/chatRoom";
    }
    
    @GetMapping("/start")
    @ResponseBody
    public Map<String, Object> start(
            Principal principal) {

        String memberId = principal.getName();

        Long roomId =
                service.createOrFindRoom(memberId);

        Map<String, Object> map =
                new HashMap<>();

        map.put("roomId", roomId);

        return map;
    }
    
    @GetMapping("/messages")
    @ResponseBody
    public List<ChatMessageVO> messages(
            @RequestParam Long roomId){

        return service.getMessages(roomId);
    }
}