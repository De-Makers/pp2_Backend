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

    //room test controller
    @PostMapping("/chat/test/room")
    public int createRoom(@RequestBody String roomName){
        return chatService.createRoomAndGetRoomId(roomName);
//        return ChatRoomDTO.create(roomName);
    }

    @GetMapping("/chat/list")
    public List<ChatRoomVO> getChatRoomList(@RequestHeader("Authorization") String token){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.getChatRoomUidList(userUid);
    }

    //room_session_id 알아내는 test controller
    @GetMapping("/chat/test/{room_uid}")
    public String getChatRoomSessionUid(@PathVariable(value = "room_uid") int roomUid){
        return chatService.getSessionId(roomUid);
    }
}
