package sg.dm.pp2.chatServer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    @PostMapping("/chat/room")
    public ChatRoomDTO createRoom(@RequestParam("room_name") String roomName){
        return ChatRoomDTO.create(roomName);
    }
}
