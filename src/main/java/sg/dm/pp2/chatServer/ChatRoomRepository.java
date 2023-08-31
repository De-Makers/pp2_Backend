package sg.dm.pp2.chatServer;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChatRoomRepository {
//
//    private Map<String, ChatRoomDTO> chatRoomDTOMap; //DB에 저장
//
//    @PostConstruct
//    private void init(){
//        chatRoomDTOMap = new LinkedHashMap<>();
//    }
//
//    public List<ChatRoomDTO> findAllRooms(){
//        //채팅방 생성 순서 최근 순으로 반환
//        List<ChatRoomDTO> result = new ArrayList<>(chatRoomDTOMap.values());
//        Collections.reverse(result);
//
//        return result;
//    }
//
//    public ChatRoomDTO findRoomById(String id){
//        return chatRoomDTOMap.get(id);
//    }
//
//    public ChatRoomDTO createChatRoomDTO(){
//        ChatRoomDTO room = ChatRoomDTO.create();
//        chatRoomDTOMap.put(room.getRoomId(), room);
//
//        return room;
//    }
}