package chat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chat.model.ChatMessageVO;
import chat.model.ChatRoomVO;
import chat.repository.ChatDAO;
import chat.repository.ChatRoomDAO;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatDAO chatDAO;

    @Autowired
    private ChatRoomDAO chatRoomDAO;

    @Override
    public List<ChatRoomVO> getRoomList() {
        return chatRoomDAO.selectAllRooms();
    }

    @Override
    public void save(ChatMessageVO chat) {
        chatDAO.insertMessage(chat);
    }

    @Override
    public List<ChatMessageVO> getMessages(Long roomId) {
        return chatDAO.getMessages(roomId);
    }

    @Override
    public Long createOrFindRoom(String memberId) {

        String adminId = "admin";

        Long roomId = chatDAO.findRoom(memberId, adminId);

        if (roomId != null) {
            return roomId;
        }

        ChatRoomVO vo = new ChatRoomVO();
        vo.setUsername1(memberId);
        vo.setUsername2(adminId);

        chatDAO.createRoom(vo);

        return vo.getRoomId();
    }
    
    
}