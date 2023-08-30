package sg.dm.pp2.chatServer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.chatServer.entity.ChatTable;
import sg.dm.pp2.chatServer.entity.ChatroomSessionTable;
import sg.dm.pp2.chatServer.entity.ChatroomUserTable;
import sg.dm.pp2.chatServer.repository.ChatRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomSessionRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomUserRepository;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.service.vo.ProfileListVO;

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
    public void saveMessage(ChatMessageDTO message){
        Optional<ChatroomSessionTable> chatroomSessionTableOptional = chatRoomSessionRepository.findBySessionId(message.getRoomId());

        if(chatroomSessionTableOptional.isPresent()){
            int chatroomUid = chatroomSessionTableOptional.get().getChatroomUid();
            ChatTable chatTable = ChatTable.builder()
                    .chatRoomUid(chatroomUid)
                    .userUid(message.getWriterUid())
                    .message(message.getMessage())
                    .registeredDatetime(LocalDateTime.now())
                    .build();

            chatRepository.save(chatTable);
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
                    .chatRoomUid(x.getChatroomUid())
                    .build())
                .collect(Collectors.toList());

        return chatRoomList;
    }
}