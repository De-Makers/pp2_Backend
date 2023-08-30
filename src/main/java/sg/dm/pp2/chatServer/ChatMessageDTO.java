package sg.dm.pp2.chatServer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private String roomId;
    private int writerUid;
    private String message;
}