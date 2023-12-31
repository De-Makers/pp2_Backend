package sg.dm.pp2.chatServer.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ChatRoomDTO {
    private String roomId;
    private Set<WebSocketSession> sessions = new HashSet<>();
    //WebSocketSession은 Spring에서 Websocket Connection이 맺어진 세션

    public static ChatRoomDTO create(){
        ChatRoomDTO room = new ChatRoomDTO();

        //TODO: 중복체크
        room.roomId = UUID.randomUUID().toString();
        return room;
    }
}