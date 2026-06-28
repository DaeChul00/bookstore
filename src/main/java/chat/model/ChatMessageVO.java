package chat.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageVO {

    private Long id;
    private Long roomId;
    private String sender;
    private String message;
    private LocalDateTime sendTime;
    private boolean isRead;
}