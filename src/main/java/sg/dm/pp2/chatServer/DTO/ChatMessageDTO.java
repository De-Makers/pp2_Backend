package sg.dm.pp2.chatServer.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO {
    private int chatroomUid;
    private int writerUid;
    private int typeUid;
    private String message;
}