package sg.dm.pp2.chatServer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.chatServer.VO.ChatRoomVO;
import sg.dm.pp2.chatServer.VO.LastMessageVO;
import sg.dm.pp2.chatServer.VO.ReadCheckVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    @Autowired
    private TokenAuthUtil tokenAuthUtil;
    @Autowired
    private ChatService chatService;

    @GetMapping("/chat/list")
    public List<ChatRoomVO> getChatRoomList(@RequestHeader("Authorization") String token){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.getChatRoomUidList(userUid);
    }
    @GetMapping("/chat/message/last/check/{chatroom_uid}/{user_uid}")
    public ReadCheckVO getLaskCheck(
            @PathVariable(value = "chatroom_uid") int chatroomUid,
            @PathVariable(value = "user_uid") int userUid
    ){
        return chatService.getReadCheck(chatroomUid, userUid);
    }

    @GetMapping("chat/message/last/{chatroom_uid}")
    public LastMessageVO getLastMessage(@PathVariable(value = "chatroom_uid") int chatroomUid){
        return chatService.getLastMessage(chatroomUid);
    }


    //--------------------FOR TEST------------------------
    //room test controller
    @PostMapping("/chat/test/room")
    public int createRoom(){
        return chatService.createRoomAndGetRoomId();
//        return ChatRoomDTO.create(roomName);
    }
    //room_session_id 알아내는 test controller
    @GetMapping("/chat/test/{room_uid}")
    public String getChatRoomSessionUid(@PathVariable(value = "room_uid") int roomUid){
        return chatService.getSessionId(roomUid);
    }
}
