package sg.dm.pp2.chatServer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import sg.dm.pp2.chatServer.DTO.ChatMessageDTO;
import sg.dm.pp2.chatServer.VO.MessageSessionVO;
import sg.dm.pp2.chatServer.VO.MessageVO;

@Controller
@RequiredArgsConstructor
public class StompChatController {
    @Autowired
    private ChatService chatService;

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
//    @MessageMapping(value = "/chat/enter")
//    public void enter(ChatMessageDTO message){
//        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
//        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
//    }

    @MessageMapping(value = "/chat/message")
    public void messageText(ChatMessageDTO message){
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

//    @MessageMapping(value = "/chat/message/image")
//    public void messageImage(ChatMessageDTO message){
//        String sessionId = chatService.saveMessageAndReturnSessionId(message);
//        template.convertAndSend("/sub/chat/room/" + sessionId, message);
//    }

}
