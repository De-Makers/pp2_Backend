package sg.dm.pp2.chatServer;

import org.springframework.data.domain.Pageable;
import sg.dm.pp2.chatServer.VO.ChatRoomVO;
import sg.dm.pp2.chatServer.VO.LastMessageVO;
import sg.dm.pp2.chatServer.VO.MessageVO;
import sg.dm.pp2.chatServer.VO.ReadCheckVO;

import java.util.List;

public interface ChatService {
    String saveMessageAndReturnSessionId(ChatMessageDTO message);

    List<ChatRoomVO> getChatRoomUidList(int userUid);

    ReadCheckVO getReadCheck(int chatroomUid, int userUid);

    LastMessageVO getLastMessage(int chatroomUid);

    List<MessageVO> getAllMessage(int chatroomUid, Pageable pageable);



    //TODO : -------------FOR TEST----------
    String getSessionId(int rooomUid);

    int createRoomAndGetRoomId();
}
