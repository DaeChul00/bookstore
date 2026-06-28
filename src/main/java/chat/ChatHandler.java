package chat;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import chat.model.ChatMessageVO;
import chat.service.ChatService;

@Component
public class ChatHandler extends TextWebSocketHandler {

    // roomId -> sessions
    private static final Map<Long, Set<WebSocketSession>> roomSessions
            = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        if (session.getPrincipal() == null) {
            session.close();
            return;
        }

        Object roomObj = session.getAttributes().get("roomId");
        if (roomObj == null) {
            session.close();
            return;
        }

        Long roomId = (Long) roomObj;
        String userId = session.getPrincipal().getName();

        roomSessions
                .computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                .add(session);

        System.out.println("WS CONNECT : " + userId + " / room : " + roomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        try {
            ChatMessageVO chat = mapper.readValue(message.getPayload(), ChatMessageVO.class);

            // 1. DB 저장
            chatService.save(chat);

            // 2. JSON 변환
            String json = mapper.writeValueAsString(chat);

            Long roomId = chat.getRoomId();

            Set<WebSocketSession> sessions = roomSessions.get(roomId);

            if (sessions == null) return;

            // 3. broadcast
            for (WebSocketSession s : sessions) {
                if (s != null && s.isOpen()) {
                    synchronized (s) {
                        s.sendMessage(new TextMessage(json));
                    }
                }
            }

            System.out.println("BROADCAST DONE room=" + roomId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        Object roomObj = session.getAttributes().get("roomId");

        if (roomObj != null) {
            Long roomId = (Long) roomObj;

            Set<WebSocketSession> sessions = roomSessions.get(roomId);

            if (sessions != null) {
                sessions.remove(session);

                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }

        if (session.getPrincipal() != null) {
            System.out.println("WS DISCONNECT : " + session.getPrincipal().getName());
        }
    }
}