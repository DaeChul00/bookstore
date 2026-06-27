package chat.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import chat.model.ChatMessageVO;
import chat.model.ChatRoomVO;

public interface ChatDAO {

    // 채팅 메시지 저장
    int insertMessage(ChatMessageVO vo);

    // 채팅 내역 조회
    List<ChatMessageVO> getMessages(Long roomId);

    // 채팅방 생성
    int createRoom(ChatRoomVO vo);

    // 채팅방 조회
    ChatRoomVO getRoom(Long roomId);

    // 상대방 아이디 조회
    String findReceiver(@Param("roomId") Long roomId,
                        @Param("sender") String sender);
    
    Long findRoom(@Param("userName1") String userName1,
            @Param("userName2") String userName2);
    
    
}