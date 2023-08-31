package sg.dm.pp2.chatServer;

import sg.dm.pp2.chatServer.VO.ChatRoomVO;
import sg.dm.pp2.chatServer.VO.LastMessageVO;
import sg.dm.pp2.chatServer.VO.ReadCheckVO;

import java.util.List;

public interface ChatService {
    String saveMessageAndReturnSessionId(ChatMessageDTO message);

    List<ChatRoomVO> getChatRoomUidList(int userUid);

    String getSessionId(int rooomUid);

    int createRoomAndGetRoomId();

    ReadCheckVO getReadCheck(int chatroomUid, int userUid);

    LastMessageVO getLastMessage(int chatroomUid);
}
