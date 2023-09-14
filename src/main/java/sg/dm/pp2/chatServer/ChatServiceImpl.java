package sg.dm.pp2.chatServer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sg.dm.pp2.chatServer.DTO.ChatMessageDTO;
import sg.dm.pp2.chatServer.DTO.ChatRoomDTO;
import sg.dm.pp2.chatServer.DTO.ScheduleDTO;
import sg.dm.pp2.chatServer.VO.*;
import sg.dm.pp2.chatServer.entity.ChatTable;
import sg.dm.pp2.chatServer.entity.ChatroomSessionTable;
import sg.dm.pp2.chatServer.entity.ChatroomUserTable;
import sg.dm.pp2.chatServer.repository.ChatRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomSessionRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomUserRepository;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.StudentInfoRepository;
import sg.dm.pp2.service.S3Upload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
            log.info("isPresnt");
            String contentToTransfer = "";
            switch (message.getTypeUid()){
                case 1:
                    log.info("message type1");
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
            log.info("CHATROOM_NOT_FOUND");
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

    @Override
    public ScheduleVO initSchedule(int chatroomUid, int userUid){
        Optional<ChatroomUserTable> chatroomUserTableOptional = chatRoomUserRepository.findByChatroomUidAndUserUid(chatroomUid, userUid);
        if(chatroomUserTableOptional.isPresent()){
            //0으로 초기화
            ChatroomUserTable chatroomUserTable = chatroomUserTableOptional.get();
            chatroomUserTable.setMon(0);
            chatroomUserTable.setTues(0);
            chatroomUserTable.setWed(0);
            chatroomUserTable.setThur(0);
            chatroomUserTable.setFri(0);
            chatroomUserTable.setSat(0);
            chatroomUserTable.setSun(0);
            chatRoomUserRepository.save(chatroomUserTable);

            ScheduleVO scheduleVO = ScheduleVO.builder()
                    .chatroomUid(chatroomUid)
                    .userUid(userUid)
                    .mon(0)
                    .tues(0)
                    .wed(0)
                    .thur(0)
                    .fri(0)
                    .sat(0)
                    .sun(0)
                    .build();
            return scheduleVO;
        }
        else{
            throw new NotFoundException("CHATROOM_NOT_FOUND");
        }
    }

    @Override
    public List<ScheduleVO> getSchedule(int chatroomUid, int userUid){
        boolean[][] user1 = new boolean[8][7];
        boolean[][] user2 = new boolean[8][7];
        int[] dayList1 = new int[7];
        int[] dayList2 = new int [7];
        int[] result = new int[7];
        Arrays.fill(result, 0);
        int k;

        Optional<ChatroomUserTable> chatroomUserTableOptional = chatRoomUserRepository.findByChatroomUidAndUserUid(chatroomUid, userUid);
        if (chatroomUserTableOptional.isPresent()) {
            dayList1[0] = chatroomUserTableOptional.get().getMon();
            dayList1[1] = chatroomUserTableOptional.get().getTues();
            dayList1[2] = chatroomUserTableOptional.get().getWed();
            dayList1[3] = chatroomUserTableOptional.get().getThur();
            dayList1[4] = chatroomUserTableOptional.get().getFri();
            dayList1[5] = chatroomUserTableOptional.get().getSat();
            dayList1[6] = chatroomUserTableOptional.get().getSun();
        }
        //상대 useruid의 값 받기
        List<ChatroomUserTable> chatroomUserTableList = chatRoomUserRepository.findAllByChatroomUid(chatroomUid);
        for (k = 0; k < 2; k++) {
            if (chatroomUserTableList.get(k).getUserUid() != userUid) {
                dayList2[0] = chatroomUserTableList.get(k).getMon();
                dayList2[1] = chatroomUserTableList.get(k).getTues();
                dayList2[2] = chatroomUserTableList.get(k).getWed();
                dayList2[3] = chatroomUserTableList.get(k).getThur();
                dayList2[4] = chatroomUserTableList.get(k).getFri();
                dayList2[5] = chatroomUserTableList.get(k).getSat();
                dayList2[6] = chatroomUserTableList.get(k).getSun();
                break;
            }
        }
        List<ScheduleVO> scheduleVOList = new ArrayList<>();
        scheduleVOList.add(ScheduleVO.builder()
                .chatroomUid(chatroomUid)
                .userUid(userUid)
                .mon(dayList1[0])
                .tues(dayList1[1])
                .wed(dayList1[2])
                .thur(dayList1[3])
                .fri(dayList1[4])
                .sat(dayList1[5])
                .sun(dayList1[6])
                .build()
        );

        scheduleVOList.add(ScheduleVO.builder()
                .chatroomUid(chatroomUid)
                .userUid(chatroomUserTableList.get(k).getUserUid())
                .mon(dayList2[0])
                .tues(dayList2[1])
                .wed(dayList2[2])
                .thur(dayList2[3])
                .fri(dayList2[4])
                .sat(dayList2[5])
                .sun(dayList2[6])
                .build()
        );

        for(int i=0;i<7;i++){
            if(dayList1[i] > 255 || dayList2[i] > 255){
                throw new NotFoundException("INPUT VALUE ERROR!!");
            }
        }

        for(int i=0;i<7;i++){
            //내꺼 계산
            while(dayList1[i] > 0){
                if(dayList1[i] < 0){
                    throw new NotFoundException("INPUT VALUE ERROR!!");
                }
                if(dayList1[i] >= 128){
                    dayList1[i] -= 128;
                    user1[7][i] = true;
                }
                else if(dayList1[i] >= 64){
                    dayList1[i] -= 64;
                    user1[6][i] = true;
                }
                else if(dayList1[i] >= 32){
                    dayList1[i] -= 32;
                    user1[5][i] = true;
                }
                else if(dayList1[i] >= 16){
                    dayList1[i] -= 16;
                    user1[4][i] = true;
                }
                else if(dayList1[i] >= 8){
                    dayList1[i] -= 8;
                    user1[3][i] = true;
                }
                else if(dayList1[i] >= 4){
                    dayList1[i] -= 4;
                    user1[2][i] = true;
                }
                else if(dayList1[i] >= 2){
                    dayList1[i] -= 2;
                    user1[1][i] = true;
                }
                else if(dayList1[i] >= 1){
                    dayList1[i] -= 1;
                    user1[0][i] = true;
                }
            }

            //상대꺼 계산
            while(dayList2[i] > 0){
                if(dayList2[i] < 0){
                    throw new NotFoundException("INPUT VALUE ERROR!!");
                }
                if(dayList2[i] >= 128){
                    dayList2[i] -= 128;
                    user2[7][i] = true;
                }
                else if(dayList2[i] >= 64){
                    dayList2[i] -= 64;
                    user2[6][i] = true;
                }
                else if(dayList2[i] >= 32){
                    dayList2[i] -= 32;
                    user2[5][i] = true;
                }
                else if(dayList2[i] >= 16){
                    dayList2[i] -= 16;
                    user2[4][i] = true;
                }
                else if(dayList2[i] >= 8){
                    dayList2[i] -= 8;
                    user2[3][i] = true;
                }
                else if(dayList2[i] >= 4){
                    dayList2[i] -= 4;
                    user2[2][i] = true;
                }
                else if(dayList2[i] >= 2){
                    dayList2[i] -= 2;
                    user2[1][i] = true;
                }
                else if(dayList2[i] >= 1){
                    dayList2[i] -= 1;
                    user2[0][i] = true;
                }
            }
        }

        //겹치는 날 계산
        for(int i=0;i<8;i++){
            for(int j=0;j<7;j++){
                if(user1[i][j] == true && user2[i][j] == true){
                    result[j] += Math.pow(2,i);
                }
            }
        }


        //결과값 빌드
        scheduleVOList.add(ScheduleVO.builder()
                .chatroomUid(chatroomUid)
                .userUid(userUid)
                .mon(result[0])
                .tues(result[1])
                .wed(result[2])
                .thur(result[3])
                .fri(result[4])
                .sat(result[5])
                .sun(result[6])
                .build()
        );

        return scheduleVOList;
    }

    @Override
    public List<ScheduleVO> postSchedule(int chatroomUid, int userUid, ScheduleDTO scheduleDTO){
        boolean[][] user1 = new boolean[8][7];
        boolean[][] user2 = new boolean[8][7];
        int[] dayList1 = new int[7];
        int[] dayList2 = new int [7];
        int[] result = new int[7];
        Arrays.fill(result, 0);
        int k;

        //내 schedule 저장
        Optional<ChatroomUserTable> chatroomUserTableOptional = chatRoomUserRepository.findByChatroomUidAndUserUid(chatroomUid, userUid);
        if (chatroomUserTableOptional.isPresent()) {

            ChatroomUserTable chatroomUserTable = chatroomUserTableOptional.get();
            chatroomUserTable.setMon(scheduleDTO.getMon());
            chatroomUserTable.setTues(scheduleDTO.getTues());
            chatroomUserTable.setWed(scheduleDTO.getWed());
            chatroomUserTable.setThur(scheduleDTO.getThur());
            chatroomUserTable.setFri(scheduleDTO.getFri());
            chatroomUserTable.setSat(scheduleDTO.getSat());
            chatroomUserTable.setSun(scheduleDTO.getSun());
            chatRoomUserRepository.save(chatroomUserTable);

            dayList1[0] = scheduleDTO.getMon();
            dayList1[1] = scheduleDTO.getTues();
            dayList1[2] = scheduleDTO.getWed();
            dayList1[3] = scheduleDTO.getThur();
            dayList1[4] = scheduleDTO.getFri();
            dayList1[5] = scheduleDTO.getSat();
            dayList1[6] = scheduleDTO.getSun();
        }
        else{
            throw new NotFoundException("USER_NOT_FOUND");
        }

        //상대 useruid의 값 받기
        List<ChatroomUserTable> chatroomUserTableList = chatRoomUserRepository.findAllByChatroomUid(chatroomUid);
        for (k = 0; k < 2; k++) {
            if (chatroomUserTableList.get(k).getUserUid() != userUid) {
                log.info("my uid : " + userUid);
                log.info("other uid : " + chatroomUserTableList.get(k).getUserUid());
                dayList2[0] = chatroomUserTableList.get(k).getMon();
                dayList2[1] = chatroomUserTableList.get(k).getTues();
                dayList2[2] = chatroomUserTableList.get(k).getWed();
                dayList2[3] = chatroomUserTableList.get(k).getThur();
                dayList2[4] = chatroomUserTableList.get(k).getFri();
                dayList2[5] = chatroomUserTableList.get(k).getSat();
                dayList2[6] = chatroomUserTableList.get(k).getSun();
                break;
            }
        }

        List<ScheduleVO> scheduleVOList = new ArrayList<>();
        scheduleVOList.add(ScheduleVO.builder()
                .chatroomUid(chatroomUid)
                .userUid(userUid)
                .mon(dayList1[0])
                .tues(dayList1[1])
                .wed(dayList1[2])
                .thur(dayList1[3])
                .fri(dayList1[4])
                .sat(dayList1[5])
                .sun(dayList1[6])
                .build()
        );

        scheduleVOList.add(ScheduleVO.builder()
                .chatroomUid(chatroomUid)
                .userUid(chatroomUserTableList.get(k).getUserUid())
                .mon(dayList2[0])
                .tues(dayList2[1])
                .wed(dayList2[2])
                .thur(dayList2[3])
                .fri(dayList2[4])
                .sat(dayList2[5])
                .sun(dayList2[6])
                .build()
        );
        System.out.println("dayList2 : ");
        for(int i=0;i<7;i++)
            System.out.print(" " + dayList2[i]);

        for(int i=0;i<7;i++){
            if(dayList1[i] > 255 || dayList2[i] > 255){
                throw new NotFoundException("INPUT VALUE ERROR!!");
            }
        }

        for(int i=0;i<7;i++){
            //내꺼 계산
            while(dayList1[i] > 0){
                if(dayList1[i] < 0){
                    throw new NotFoundException("INPUT VALUE ERROR!!");
                }
                if(dayList1[i] >= 128){
                    dayList1[i] -= 128;
                    user1[7][i] = true;
                }
                else if(dayList1[i] >= 64){
                    dayList1[i] -= 64;
                    user1[6][i] = true;
                }
                else if(dayList1[i] >= 32){
                    dayList1[i] -= 32;
                    user1[5][i] = true;
                }
                else if(dayList1[i] >= 16){
                    dayList1[i] -= 16;
                    user1[4][i] = true;
                }
                else if(dayList1[i] >= 8){
                    dayList1[i] -= 8;
                    user1[3][i] = true;
                }
                else if(dayList1[i] >= 4){
                    dayList1[i] -= 4;
                    user1[2][i] = true;
                }
                else if(dayList1[i] >= 2){
                    dayList1[i] -= 2;
                    user1[1][i] = true;
                }
                else if(dayList1[i] >= 1){
                    dayList1[i] -= 1;
                    user1[0][i] = true;
                }
            }

            //상대꺼 계산
            while(dayList2[i] > 0){
                if(dayList2[i] < 0){
                    throw new NotFoundException("INPUT VALUE ERROR!!");
                }
                if(dayList2[i] >= 128){
                    dayList2[i] -= 128;
                    user2[7][i] = true;
                }
                else if(dayList2[i] >= 64){
                    dayList2[i] -= 64;
                    user2[6][i] = true;
                }
                else if(dayList2[i] >= 32){
                    dayList2[i] -= 32;
                    user2[5][i] = true;
                }
                else if(dayList2[i] >= 16){
                    dayList2[i] -= 16;
                    user2[4][i] = true;
                }
                else if(dayList2[i] >= 8){
                    dayList2[i] -= 8;
                    user2[3][i] = true;
                }
                else if(dayList2[i] >= 4){
                    dayList2[i] -= 4;
                    user2[2][i] = true;
                }
                else if(dayList2[i] >= 2){
                    dayList2[i] -= 2;
                    user2[1][i] = true;
                }
                else if(dayList2[i] >= 1){
                    dayList2[i] -= 1;
                    user2[0][i] = true;
                }
            }
        }
//
//        System.out.println("USer1 : ");
//        for(int i=0;i<8;i++){
//            for(int j=0;j<7;j++){
//                System.out.println(" " + user1[i][j]);
//            }
//            System.out.println("\n");
//        }
//        System.out.println("USer2 : ");
//        for(int i=0;i<8;i++){
//            for(int j=0;j<7;j++){
//                System.out.println(" " + user2[i][j]);
//            }
//            System.out.println("\n");
//        }


        //겹치는 날 계산
        for(int i=0;i<8;i++){
            for(int j=0;j<7;j++){
                if(user1[i][j] == true && user2[i][j] == true){
                    result[j] += Math.pow(2,i);
                    log.info("result += " + Math.pow(2,j));
                }
            }
        }

        //결과값 빌드
        scheduleVOList.add(ScheduleVO.builder()
                .chatroomUid(chatroomUid)
                .userUid(userUid)
                .mon(result[0])
                .tues(result[1])
                .wed(result[2])
                .thur(result[3])
                .fri(result[4])
                .sat(result[5])
                .sun(result[6])
                .build()
        );

        return scheduleVOList;
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
