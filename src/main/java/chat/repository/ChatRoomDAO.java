package chat.repository;

import java.util.List;

import chat.model.ChatRoomVO;

public interface ChatRoomDAO {
    List<ChatRoomVO> selectAllRooms();
}