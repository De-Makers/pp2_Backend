package sg.dm.pp2.chatServer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.chatServer.VO.ChatRoomVO;
import sg.dm.pp2.chatServer.VO.LastMessageVO;
import sg.dm.pp2.chatServer.VO.ReadCheckVO;
import sg.dm.pp2.chatServer.entity.ChatTable;
import sg.dm.pp2.chatServer.entity.ChatroomSessionTable;
import sg.dm.pp2.chatServer.entity.ChatroomUserTable;
import sg.dm.pp2.chatServer.repository.ChatRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomSessionRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomUserRepository;
import sg.dm.pp2.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService{
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatRoomSessionRepository chatRoomSessionRepository;
    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;

    @Override
    public String saveMessageAndReturnSessionId(ChatMessageDTO message){
        Optional<ChatroomSessionTable> chatroomSessionTableOptional = chatRoomSessionRepository.findByChatroomUid(message.getRoomUid());
        if(chatroomSessionTableOptional.isPresent()){
            //메세지 저장
            ChatTable chatTable = ChatTable.builder()
                    .chatroomUid(message.getRoomUid())
                    .userUid(message.getWriterUid())
                    .message(message.getMessage())
                    .registeredDatetime(LocalDateTime.now())
                    .build();
            chatRepository.save(chatTable);

            //나를 제외한 상대방 read_check false로 set
            long chatCount = chatRoomUserRepository.countByChatroomUid(message.getRoomUid());
            if(chatCount>0) {
                List<ChatroomUserTable> chatroomUserTableList = chatRoomUserRepository.findAllByChatroomUid(message.getRoomUid());
                for (int i = 0; i < chatCount; i++) {
                    if (chatroomUserTableList.get(i).getUserUid() != message.getWriterUid()) {
                        chatroomUserTableList.get(i).setReadCheck(false);
                        chatRoomUserRepository.save(chatroomUserTableList.get(i));
                    }
                }
            }


            //chatroom의 session_id 리턴
            String sessionId = chatroomSessionTableOptional.get().getSessionId();
            return sessionId;
        }
        else{
            //session_id로 채팅방 uid를 찾지 못함
            throw new NotFoundException("CHATROOM_NOT_FOUND");
        }
    }

    @Override
    public List<ChatRoomVO> getChatRoomUidList(int userUid){
        List<ChatroomUserTable> chatroomUserTableList = chatRoomUserRepository.findAllByUserUid(userUid);
        List<ChatRoomVO> chatRoomList = chatroomUserTableList.stream().map(x -> ChatRoomVO.builder()
                    .chatroomUid(x.getChatroomUid())
                    .build())
                .collect(Collectors.toList());

        return chatRoomList;
    }

    @Override
    public ReadCheckVO getReadCheck(int chatroomUid, int userUid){
        Optional<ChatroomUserTable> chatroomUserTableOptional = chatRoomUserRepository.findByChatroomUidAndUserUid(chatroomUid, userUid);
        if(chatroomUserTableOptional.isPresent()){
            ReadCheckVO readCheckVO = ReadCheckVO.builder()
                    .readCheck(chatroomUserTableOptional.get().isReadCheck())
                    .build();
            return readCheckVO;
        }
        else{
            throw new NotFoundException("CHATROOM_NOT_FOUND");
        }
    }

    @Override
    public LastMessageVO getLastMessage(int chatroomUid){
        Optional<ChatTable> chatTableOptional = chatRepository.findTopByChatroomUidOrderByChatUidDesc(chatroomUid);
        if(chatTableOptional.isPresent()){
            LastMessageVO lastMessageVO = LastMessageVO.builder()
                    .message(chatTableOptional.get().getMessage())
                    .build();
            return lastMessageVO;
        }
        else{
            LastMessageVO lastMessageVO = LastMessageVO.builder()
                    .message(null)
                    .build();
            return lastMessageVO;
        }
    }


    //---------------------TEST-----------------------------
    //for test
    @Override
    public String getSessionId(int roomUid){
        Optional<ChatroomSessionTable> chatroomSessionTableOptional = chatRoomSessionRepository.findByChatroomUid(roomUid);
        if(chatroomSessionTableOptional.isPresent()){
            return chatroomSessionTableOptional.get().getSessionId();
        }
        else{
            throw new NotFoundException("CHATROOM_NOT_FOUND");
        }
    }

    //for test
    @Override
    public int createRoomAndGetRoomId(){
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.create();

        //session table db에 채팅방 저장
        ChatroomSessionTable chatroomSessionTable = ChatroomSessionTable.builder()
                .sessionId(chatRoomDTO.getRoomId())
                .build();
        ChatroomSessionTable savedChatRoomSession = chatRoomSessionRepository.save(chatroomSessionTable);

        return savedChatRoomSession.getChatroomUid();
    }
}
