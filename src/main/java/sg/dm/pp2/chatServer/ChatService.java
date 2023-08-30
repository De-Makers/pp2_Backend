package sg.dm.pp2.chatServer;

import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessageDTO message);

    List<ChatRoomVO> getChatRoomUidList(int userUid);
}