package sg.dm.pp2.chatServer.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageSessionVO {
    private String sessionId;
    private int chatUid;
    private int writerUid;
    private String message;
    private int typeUid;
    private LocalDateTime registeredDatetime;
}
