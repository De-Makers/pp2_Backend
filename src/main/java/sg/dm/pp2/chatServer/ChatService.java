package sg.dm.pp2.chatServer;

import org.springframework.data.domain.Pageable;
import sg.dm.pp2.chatServer.DTO.ChatMessageDTO;
import sg.dm.pp2.chatServer.DTO.ScheduleDTO;
import sg.dm.pp2.chatServer.VO.*;

import java.util.List;

public interface ChatService {
    String saveMessageAndReturnSessionId(ChatMessageDTO message);

    List<ChatRoomVO> getChatRoomUidList(int userUid);

    ReadCheckVO getReadCheck(int chatroomUid, int userUid);

    LastMessageVO getLastMessage(int chatroomUid);

    List<MessageVO> getAllMessage(int chatroomUid, Pageable pageable);

    ScheduleVO initSchedule(int chatroomUid, int userUid);

    ScheduleVO getSchedule(int chatroomUid, int userUid);

    ScheduleVO postSchedule(int chatroomUid, int userUid, ScheduleDTO scheduleDTO);



    //TODO : -------------FOR TEST----------
    String getSessionId(int rooomUid);

    int createRoomAndGetRoomId();
}
