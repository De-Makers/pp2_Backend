package sg.dm.pp2.chatServer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sg.dm.pp2.chatServer.VO.ChatRoomVO;
import sg.dm.pp2.chatServer.VO.LastMessageVO;
import sg.dm.pp2.chatServer.VO.MessageVO;
import sg.dm.pp2.chatServer.VO.ReadCheckVO;
import sg.dm.pp2.chatServer.entity.ChatTable;
import sg.dm.pp2.chatServer.entity.ChatroomSessionTable;
import sg.dm.pp2.chatServer.entity.ChatroomUserTable;
import sg.dm.pp2.chatServer.repository.ChatRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomSessionRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomUserRepository;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.entity.impl.CustomMultipartFile;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.StudentInfoRepository;
import sg.dm.pp2.service.S3Upload;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatRoomSessionRepository chatRoomSessionRepository;
    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;
    @Autowired
    private S3Upload s3Upload;
    @Autowired
    private StudentInfoRepository studentInfoRepository;

    @Override
    public String saveMessageAndReturnSessionId(ChatMessageDTO message){
        Optional<ChatroomSessionTable> chatroomSessionTableOptional = chatRoomSessionRepository.findByChatroomUid(message.getRoomUid());
        if (chatroomSessionTableOptional.isPresent()) {
            String contentToTransfer = "";
            switch (message.getTypeUid()){
                case 1:
                    contentToTransfer = message.getMessage();
                    break;
                case 2:
//                    String fileBase64 = message.getMessage();
//                    Base64.Decoder decoder = Base64.getDecoder();
//                    byte[] decodedBytes = decoder.decode(fileBase64.getBytes());
//                    log.info("Bytecode : " + decodedBytes);
//                    CustomMultipartFile customMultipartFile = new CustomMultipartFile(decodedBytes);
//                    log.info("size: " + customMultipartFile.getSize());
//                    try {
//                        contentToTransfer = s3Upload.upload(customMultipartFile);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
                    contentToTransfer = message.getMessage();
                    break;
                case 3:
                    contentToTransfer += "안녕하세요!";
                    break;
                case 4:
                    contentToTransfer += "반가워요!";
                    break;
                case 5:
                    contentToTransfer += "밥약하고 싶어요!";
                    break;
                case 6:
                    contentToTransfer += "좋아요!";
                    break;
                default:
                    throw new NotFoundException("MESSAGE_TYPE_NOT_FOUND");
            }
            message.setMessage(contentToTransfer);
            //메세지 저장
            ChatTable chatTable = ChatTable.builder()
                    .chatroomUid(message.getRoomUid())
                    .userUid(message.getWriterUid())
                    .message(contentToTransfer)
                    .typeUid(message.getTypeUid())
                    .registeredDatetime(LocalDateTime.now())
                    .build();
            chatRepository.save(chatTable);

            //나를 제외한 상대방 read_check false로 set
            List<ChatroomUserTable> chatroomUserTableList = chatRoomUserRepository.findAllByChatroomUid(message.getRoomUid());
            long chatCount = chatroomUserTableList.stream().count();
            log.info("roomuid : " + message.getRoomUid());
            log.info("chatCount : " + chatCount);
            if (chatCount > 0) {
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
        } else {
            //session_id로 채팅방 uid를 찾지 못함
            throw new NotFoundException("CHATROOM_NOT_FOUND");
        }
    }

    @Override
    public List<ChatRoomVO> getChatRoomUidList(int userUid) {
        List<ChatroomUserTable> chatroomUserTableList = chatRoomUserRepository.findAllByUserUid(userUid);
        List<ChatRoomVO> chatRoomList = chatroomUserTableList.stream().map(x -> ChatRoomVO.builder()
                        .chatroomUid(x.getChatroomUid())
                        .build())
                .collect(Collectors.toList());

        return chatRoomList;
    }

    @Override
    public ReadCheckVO getReadCheck(int chatroomUid, int userUid) {
        Optional<ChatroomUserTable> chatroomUserTableOptional = chatRoomUserRepository.findByChatroomUidAndUserUid(chatroomUid, userUid);
        if (chatroomUserTableOptional.isPresent()) {
            ReadCheckVO readCheckVO = ReadCheckVO.builder()
                    .readCheck(chatroomUserTableOptional.get().isReadCheck())
                    .build();
            return readCheckVO;
        } else {
            throw new NotFoundException("CHATROOM_NOT_FOUND");
        }
    }

    @Override
    public LastMessageVO getLastMessage(int chatroomUid) {
        Optional<ChatTable> chatTableOptional = chatRepository.findTopByChatroomUidOrderByChatUidDesc(chatroomUid);
        if (chatTableOptional.isPresent()) {
            LastMessageVO lastMessageVO = LastMessageVO.builder()
                    .message(chatTableOptional.get().getMessage())
                    .build();
            return lastMessageVO;
        } else {
            LastMessageVO lastMessageVO = LastMessageVO.builder()
                    .message(null)
                    .build();
            return lastMessageVO;
        }
    }

    @Override
    public List<MessageVO> getAllMessage(int chatroomUid, Pageable pageable){
        List<ChatTable> chatTableList = chatRepository.findAllByChatroomUidOrderByChatUidDesc(chatroomUid, pageable);
        if(!chatTableList.isEmpty()){
            List<MessageVO> messageVOList = chatTableList.stream().map(x -> MessageVO.builder()
                            .chatUid(x.getChatUid())
                            .writerUid(x.getUserUid())
                            .message(x.getMessage())
                            .typeUid(x.getTypeUid())
                            .registeredDatetime(x.getRegisteredDatetime())
                        .build())
                    .collect(Collectors.toList());

            return messageVOList;
        }
        else{
            return null; //TODO : 채팅이 하나도 없으면 뭘 리턴해야할까??
        }
    }


    //TODO : ---------------------TEST-----------------------------
    //for test
    @Override
    public String getSessionId(int roomUid) {
        Optional<ChatroomSessionTable> chatroomSessionTableOptional = chatRoomSessionRepository.findByChatroomUid(roomUid);
        if (chatroomSessionTableOptional.isPresent()) {
            return chatroomSessionTableOptional.get().getSessionId();
        } else {
            throw new NotFoundException("CHATROOM_NOT_FOUND");
        }
    }

    //for test
    @Override
    public int createRoomAndGetRoomId() {
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.create();

        //session table db에 채팅방 저장
        ChatroomSessionTable chatroomSessionTable = ChatroomSessionTable.builder()
                .sessionId(chatRoomDTO.getRoomId())
                .build();
        ChatroomSessionTable savedChatRoomSession = chatRoomSessionRepository.save(chatroomSessionTable);

        return savedChatRoomSession.getChatroomUid();
    }
}
