package sg.dm.pp2.chatServer;

import java.util.List;

public interface ChatService {
    String saveMessageAndReturnSessionId(ChatMessageDTO message);

    List<ChatRoomVO> getChatRoomUidList(int userUid);

    String getSessionId(int rooomUid);

    int createRoomAndGetRoomId(String roomName);
}
