package sg.dm.pp2.chatServer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.service.vo.ProfileListVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    @Autowired
    private TokenAuthUtil tokenAuthUtil;
    @Autowired
    private ChatService chatService;
//    @PostMapping("/chat/room")
//    public ChatRoomDTO createRoom(@RequestParam("room_name") String roomName){
//        return ChatRoomDTO.create(roomName);
//    }

    @GetMapping("/chat/list")
    public List<ChatRoomVO> getChatRoomList(@RequestHeader("Authorization") String token){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.getChatRoomUidList(userUid);
    }
}
