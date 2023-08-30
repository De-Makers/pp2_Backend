package sg.dm.pp2.chatServer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private int roomUid;
    private int writerUid;
    private String message;
}