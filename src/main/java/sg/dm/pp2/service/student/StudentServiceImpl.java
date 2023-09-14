package sg.dm.pp2.service.student;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.chatServer.DTO.ChatRoomDTO;
import sg.dm.pp2.chatServer.VO.LastMessageVO;
import sg.dm.pp2.chatServer.VO.ReadCheckVO;
import sg.dm.pp2.chatServer.entity.ChatTable;
import sg.dm.pp2.chatServer.entity.ChatroomSessionTable;
import sg.dm.pp2.chatServer.entity.ChatroomUserTable;
import sg.dm.pp2.chatServer.repository.ChatRepository;
import sg.dm.pp2.chatServer.repository.ChatRoomUserRepository;
import sg.dm.pp2.entity.PpRank;
import sg.dm.pp2.entity.PpRegisterState;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.exception.NotAcceptableException;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.chatServer.repository.ChatRoomSessionRepository;
import sg.dm.pp2.repository.PpRankRepository;
import sg.dm.pp2.repository.PpRegisterStateRepository;
import sg.dm.pp2.service.vo.MyProfileVO;
import sg.dm.pp2.service.vo.ProfileListVO;
import sg.dm.pp2.util.StudentIdUtil;
import sg.dm.pp2.repository.StudentInfoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentInfoRepository studentInfoRepository;

    @Autowired
    StudentIdUtil studentIdUtil;
    @Autowired
    private ChatRoomSessionRepository chatRoomSessionRepository;
    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;
    @Autowired
    private PpRegisterStateRepository ppRegisterStateRepository;
    @Autowired
    private PpRankRepository ppRankRepository;
    @Autowired
    private ChatRepository chatRepository;

    @Override
    public void saveFirstProfileForStudentInfo(
            Integer userUid,
            String studentId,
            String name,
            String message,
            String url
    ) {
        StudentInfo studentInfo = studentInfoRepository.findByUserUid(userUid).get();
        StudentIdUtil.YearAndPivot yearAndPivot = studentIdUtil.getYearAndPivotFromStudentIdAndUnivUid(studentId, studentInfo.getUnivUid());
        String studentIdPivot = yearAndPivot.getPivot();
        String studentIdYear = yearAndPivot.getYear();

        if(studentInfo.getStudentId() == null) { //처음 프로필을 만들 때만
            //-----------------------------------------여기서부터 채팅방
            int univUid = studentInfo.getUnivUid();
            List<StudentInfo> studentInfoList = studentInfoRepository.findAllByUnivUidAndStudentIdPivot(univUid, studentIdPivot);
            long pivotCount = studentInfoList.stream().count();
            log.info("pivotCount : " + pivotCount);

            //pivot이 같은 사람이 있으면
            if (pivotCount > 0) {
                //같은 사람들 수만큼
                for (int i = 0; i < pivotCount; i++) {
                    if (userUid != studentInfoList.get(i).getUserUid()) {
                        //채팅방 하나 만들고
                        ChatRoomDTO chatRoomDTO = ChatRoomDTO.create();

                        //session table db에 채팅방 저장
                        ChatroomSessionTable chatroomSessionTable = ChatroomSessionTable.builder()
                                .sessionId(chatRoomDTO.getRoomId())
                                .build();
                        ChatroomSessionTable savedChatRoomSession = chatRoomSessionRepository.save(chatroomSessionTable);

                        //내 유저아이디랑 채팅방 id db에 저장
                        ChatroomUserTable chatroomUserTable1 = ChatroomUserTable.builder()
                                .userUid(userUid)
                                .chatroomUid(savedChatRoomSession.getChatroomUid())
                                .readCheck(true)
                                .build();
                        chatRoomUserRepository.save(chatroomUserTable1);

                        //상대 유저아이디랑 채팅방 id db에 저장
                        ChatroomUserTable chatroomUserTable2 = ChatroomUserTable.builder()
                                .userUid(studentInfoList.get(i).getUserUid())
                                .chatroomUid(savedChatRoomSession.getChatroomUid())
                                .readCheck(true)
                                .build();
                        chatRoomUserRepository.save(chatroomUserTable2);
                    }
                }
            }
            //----------------------------------------------------------
            //reg_state to 2
            Optional<PpRegisterState> ppRegisterStateOptional = ppRegisterStateRepository.findByUserUid(userUid);
            PpRegisterState ppRegisterState;
            if (ppRegisterStateOptional.isPresent()) {
                ppRegisterState = ppRegisterStateOptional.get();
                ppRegisterState.setStateId(2);
            } else {
                ppRegisterState = PpRegisterState.builder()
                        .userUid(userUid)
                        .stateId(2)
                        .build();
            }
            ppRegisterStateRepository.save(ppRegisterState);

            //increase member_count in rank table
            Optional<PpRank> ppRankOptional = ppRankRepository.findByUnivUidAndStudentIdPivot(univUid, studentIdPivot);
            PpRank ppRank;
            if (ppRankOptional.isPresent()) {
                ppRank = ppRankOptional.get();
                ppRank.setMemberCount(ppRank.getMemberCount() + 1);
            } else {
                ppRank = PpRank.builder()
                        .univUid(univUid)
                        .studentIdPivot(studentIdPivot)
                        .memberCount(1)
                        .build();
            }
            ppRankRepository.save(ppRank);
        }


        //student 정보 저장
        if(url == null){
            url = studentInfo.getProfileImageUrl();
        }
        if(studentInfo.getStudentId() != null && studentId != null){
            throw new NotAcceptableException("CANNOT_CHANGE_STUDENT_ID");
        }
        studentInfoRepository.updateStudentInfoByUserUid(
                userUid = userUid,
                studentId = studentId,
                studentIdYear = studentIdYear,
                studentIdPivot = studentIdPivot,
                name = name,
                message = message,
                url = url
        );



    }

    @Override
    public MyProfileVO getMyProfile(int userUid){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
        if(studentInfoOptional.isPresent()){
            MyProfileVO myProfileVO = MyProfileVO.builder()
                    .studentId(studentInfoOptional.get().getStudentId())
                    .name(studentInfoOptional.get().getName())
                    .message(studentInfoOptional.get().getMessage())
                    .imgUrl(studentInfoOptional.get().getProfileImageUrl())
                    .build();
            return myProfileVO;
        }
        else{
            //user_uid로 student_info에서 찾지 못했음. UserDetailsServiceImpl에서 not found account로 미리 걸러짐
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }

    @Override
    public List<ProfileListVO> getFamilyProfileList(int userUid){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
        if(studentInfoOptional.isPresent()){
            int univUid = studentInfoOptional.get().getUnivUid();
            String pivot = studentInfoOptional.get().getStudentIdPivot();

            List<ProfileListVO> profileListVOS = new ArrayList<>();
            List<StudentInfo> profiles = studentInfoRepository.findAllByUnivUidAndStudentIdPivot(univUid, pivot);
            long num = profiles.stream().count();

            for(int i=0;i<num;i++){
                int resultChatroomUid = 0;
                boolean resultReadCheck;
                String resultLastMessage;
                int userId = profiles.get(i).getUserUid();
                if(userUid != userId) {
                    //chatroom Uid 찾기
                    List<ChatroomUserTable> myChatroomList = chatRoomUserRepository.findAllByUserUid(userUid);
                    long myNum = myChatroomList.stream().count();
                    List<ChatroomUserTable> yourChatroomList = chatRoomUserRepository.findAllByUserUid(userId);
                    long yourNum = yourChatroomList.stream().count();
                    for(int p=0;p<myNum;p++){
                        for(int q=0;q<yourNum;q++){
                            if(myChatroomList.get(p).getChatroomUid() == yourChatroomList.get(q).getChatroomUid()){
                                resultChatroomUid = myChatroomList.get(p).getChatroomUid();
                                break;
                            }
                        }
                        if(resultChatroomUid != 0) break;
                    }
                    //readCheck 찾기
                    if(resultChatroomUid == 0) throw new NotFoundException("CHATROOM_NOT_FOUND");
                    Optional<ChatroomUserTable> chatroomUserTableOptional = chatRoomUserRepository.findByChatroomUidAndUserUid(resultChatroomUid, userUid);
                    if (chatroomUserTableOptional.isPresent()) {
                        resultReadCheck = chatroomUserTableOptional.get().isReadCheck();
                    } else {
                        throw new NotFoundException("CHATROOM_NOT_FOUND");
                    }
                    //lastMessage 찾기
                    Optional<ChatTable> chatTableOptional = chatRepository.findTopByChatroomUidOrderByChatUidDesc(resultChatroomUid);
                    if (chatTableOptional.isPresent()) {
                        resultLastMessage = chatTableOptional.get().getMessage();
                    } else {
                        resultLastMessage = null;
                    }

                    //build
                    profileListVOS.add(ProfileListVO.builder()
                                    .userUid(profiles.get(i).getUserUid())
                                    .name(profiles.get(i).getName())
                                    .studentId(profiles.get(i).getStudentId())
                                    .studentIdYear(profiles.get(i).getStudentIdYear())
                                    .studentIdPivot(profiles.get(i).getStudentIdPivot())
                                    .message(profiles.get(i).getMessage())
                                    .imgUrl(profiles.get(i).getProfileImageUrl())
                                    .chatroomUid(resultChatroomUid)
                                    .readCheck(resultReadCheck)
                                    .lastMessage(resultLastMessage)
                            .build());
                }
            }

//            List<ProfileListVO> profileListVOS = profiles.stream().map(x -> ProfileListVO.builder()
//                            .userUid(x.getUserUid())
//                            .name(x.getName())
//                            .studentId(x.getStudentId())
//                            .studentIdYear(x.getStudentIdYear())
//                            .studentIdPivot(x.getStudentIdPivot())
//                            .message(x.getMessage())
//                            .imgUrl(x.getProfileImageUrl())
//                            .build())
//                    .collect(Collectors.toList());

            return profileListVOS;
        }
        else{
            ////user_uid로 user를 찾을 수 없음
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }

    @Override
    public ProfileListVO  getSomeoneProfile(int userUid){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
        if(studentInfoOptional.isPresent()){
            ProfileListVO profileListVO = ProfileListVO.builder()
                    .userUid(studentInfoOptional.get().getUserUid())
                    .name(studentInfoOptional.get().getName())
                    .studentId(studentInfoOptional.get().getStudentId())
                    .studentIdYear(studentInfoOptional.get().getStudentIdYear())
                    .studentIdPivot(studentInfoOptional.get().getStudentIdPivot())
                    .message(studentInfoOptional.get().getMessage())
                    .imgUrl(studentInfoOptional.get().getProfileImageUrl())
                    .build();

            return profileListVO;
        }
        else{
            //user_uid로 user를 찾을 수 없음
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }
}
