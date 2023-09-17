package sg.dm.pp2.chatServer;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.tree.from.SqmCrossJoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.dm.pp2.chatServer.DTO.ChatMessageDTO;
import sg.dm.pp2.chatServer.DTO.ScheduleDTO;
import sg.dm.pp2.chatServer.VO.*;
import sg.dm.pp2.service.S3Upload;
import sg.dm.pp2.util.TokenAuthUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    @Autowired
    private TokenAuthUtil tokenAuthUtil;
    @Autowired
    private ChatService chatService;
    @Autowired
    private S3Upload s3Upload;

    private final SimpMessagingTemplate template;


    @GetMapping("/chat/list")
    public List<ChatRoomVO> getChatRoomList(@RequestHeader("Authorization") String token){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.getChatRoomUidList(userUid);
    }
    @GetMapping("/chat/message/last/check/{chatroomUid}")
    public ReadCheckVO getLastCheck(
            @RequestHeader("Authorization") String token,
            @PathVariable(value = "chatroomUid") int chatroomUid
    ){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.getReadCheck(chatroomUid, userUid);
    }

    @GetMapping("/chat/message/last/{chatroomUid}")
    public LastMessageVO getLastMessage(@PathVariable(value = "chatroomUid") int chatroomUid){
        return chatService.getLastMessage(chatroomUid);
    }

    @GetMapping("/chat/message/all/{chatroomUid}")
    public List<MessageVO> getAllMessage(@PathVariable(value = "chatroomUid") int chatroomUid, Pageable pageable){
        return chatService.getAllMessage(chatroomUid, pageable);
    }

    @RequestMapping(value = "/chat/message/image", method = RequestMethod.POST
            ,consumes = {MediaType.APPLICATION_JSON_VALUE ,MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public void chatImageMessage(
            @RequestPart("image") MultipartFile multipartFile,
            @RequestPart("data") ChatMessageDTO message
    ) throws IllegalStateException, IOException {
        String url = s3Upload.upload(multipartFile);
        message.setMessage(url);
        MessageSessionVO messageSessionVO = chatService.saveMessageAndReturnSessionId(message);
        String sessionId = messageSessionVO.getSessionId();
        MessageVO messageVO = MessageVO.builder()
                .chatUid(messageSessionVO.getChatUid())
                .writerUid(messageSessionVO.getWriterUid())
                .message(messageSessionVO.getMessage())
                .typeUid(messageSessionVO.getTypeUid())
                .registeredDatetime(messageSessionVO.getRegisteredDatetime())
                .build();
        template.convertAndSend("/sub/chat/room/" + sessionId, messageVO);
    }

    @PostMapping("/pp/chat/{chatroomUid}/schedule/init")
    public ScheduleVO initSchedule(@RequestHeader("Authorization") String token, @PathVariable(value = "chatroomUid") int chatroomUid){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.initSchedule(chatroomUid, userUid);
    }

    @GetMapping("/pp/chat/{chatroomUid}/schedule")
    public List<ScheduleVO> getSchedule (@RequestHeader("Authorization") String token, @PathVariable(value = "chatroomUid") int chatroomUid){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.getSchedule(chatroomUid, userUid);
    }
    @PostMapping("/pp/chat/{chatroomUid}/schedule")
    public List<ScheduleVO> postSchedule(
            @RequestHeader("Authorization") String token,
            @PathVariable(value = "chatroomUid") int chatroomUid,
            @RequestBody ScheduleDTO scheduleDTO) {
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return chatService.postSchedule(chatroomUid, userUid, scheduleDTO);
    }

    //room_session_id 알아내는 controller
    @GetMapping("/chat/room/{room_uid}")
    public ChatSessionVO getChatRoomSessionUid(@PathVariable(value = "room_uid") int roomUid){
        return chatService.getSessionId(roomUid);
    }



    //TODO: --------------------FOR TEST------------------------
    //room test controller
    @PostMapping("/chat/test/room")
    public int createRoom(){
        return chatService.createRoomAndGetRoomId();
//        return ChatRoomDTO.create(roomName);
    }

}
