package sg.dm.pp2.service.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.chatServer.ChatRoomDTO;
import sg.dm.pp2.chatServer.entity.ChatroomSessionTable;
import sg.dm.pp2.chatServer.entity.ChatroomUserTable;
import sg.dm.pp2.chatServer.repository.ChatRoomUserRepository;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.chatServer.repository.ChatRoomSessionRepository;
import sg.dm.pp2.service.vo.MyProfileVO;
import sg.dm.pp2.service.vo.ProfileListVO;
import sg.dm.pp2.util.StudentIdUtil;
import sg.dm.pp2.repository.StudentInfoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentInfoRepository studentInfoRepository;

    @Autowired
    StudentIdUtil studentIdUtil;
    @Autowired
    private ChatRoomSessionRepository chatRoomSessionRepository;
    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;

    @Override
    public void saveFirstProfileForStudentInfo(
            Integer userUid,
            String studentId,
            String name,
            String message
    ) {
        StudentInfo studentInfo = studentInfoRepository.findByUserUid(userUid).get();
        StudentIdUtil.YearAndPivot yearAndPivot = studentIdUtil.getYearAndPivotFromStudentIdAndUnivUid(studentId, studentInfo.getUnivUid());
        String studentIdPivot = yearAndPivot.getPivot();
        Integer studentIdYear = yearAndPivot.getYear();

        //-----------------------------------------여기서부터 채팅방
        int univUid = studentInfo.getUnivUid();
        long pivotCount = studentInfoRepository.countByUnivUidAndStudentIdPivot(univUid, studentIdPivot);

        //같은 pivot을 같은 사람이 있으면
        if(pivotCount > 0) {
            List<StudentInfo> studentInfoList = studentInfoRepository.findAllByUnivUidAndStudentIdPivot(univUid, studentIdPivot);

            //같은 사람들 수만큼
            for(int i=0;i<pivotCount;i++){
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
        //------------------------------------------------------

        studentInfoRepository.updateStudentInfoByUserUid(
                userUid = userUid,
                studentId = studentId,
                studentIdYear = studentIdYear,
                studentIdPivot = studentIdPivot,
                name = name,
                message = message
        );

    }

    @Override
    public MyProfileVO getMyProfile(int userUid){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
        if(studentInfoOptional.isPresent()){
            MyProfileVO myProfileVO = MyProfileVO.builder()
                    .studentID(studentInfoOptional.get().getStudentId())
                    .name(studentInfoOptional.get().getName())
                    .message(studentInfoOptional.get().getMessage())
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

            List<StudentInfo> profiles = studentInfoRepository.findAllByUnivUidAndStudentIdPivot(univUid, pivot);
            List<ProfileListVO> profileListVOS = profiles.stream().map(x -> ProfileListVO.builder()
                            .userUid(x.getUserUid())
                            .name(x.getName())
                            .studentId(x.getStudentId())
                            .studentIdYear(x.getStudentIdYear())
                            .studentIdPivot(x.getStudentIdPivot())
                            .message((x.getMessage()))
                            .build())
                    .collect(Collectors.toList());

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
                    .build();

            return profileListVO;
        }
        else{
            //user_uid로 user를 찾을 수 없음
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }
}
