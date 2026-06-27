package chat.model;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class ChatRoomVO {

    private Long roomId;

    private String username1;
    private String username2;

    private Date createdAt;

    private String lastMessage;
    private Date lastTime;
}