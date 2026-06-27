package chat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chat.model.ChatMessageVO;
import chat.model.ChatRoomVO;
import chat.repository.ChatDAO;

public interface ChatService {
    List<ChatRoomVO> getRoomList();

    void save(ChatMessageVO chat);

    List<ChatMessageVO> getMessages(Long roomId);

    Long createOrFindRoom(String memberId);
}