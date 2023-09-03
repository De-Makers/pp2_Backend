package sg.dm.pp2.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sg.dm.pp2.entity.*;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.*;
import sg.dm.pp2.service.vo.RankVO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    SnsLoginRepository snsLoginRepository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    StudentInfoRepository studentInfoRepository;
    @Autowired
    private PpRegisterStateRepository ppRegisterStateRepository;
    @Autowired
    private PpRankRepository ppRankRepository;



    @Override
    public void doSignUp(
            String snsAccountUid,
            String kakaoToken,
            Integer platform
    ) {
        Integer userUid = checkInsertSnsLoginAndUpdateTokenAndReturnUserUid(snsAccountUid, kakaoToken, platform);
        insertNewUserInfoStudentInfo(userUid);

        //reg_state to 0
        Optional<PpRegisterState> ppRegisterStateOptional = ppRegisterStateRepository.findByUserUid(userUid);
        PpRegisterState ppRegisterState;
        if(ppRegisterStateOptional.isPresent()){
            ppRegisterState = ppRegisterStateOptional.get();
            ppRegisterState.setStateId(0);
        }
        else{
            ppRegisterState = PpRegisterState.builder()
                    .userUid(userUid)
                    .stateId(0)
                    .build();
        }
        ppRegisterStateRepository.save(ppRegisterState);
    }

    private Integer checkInsertSnsLoginAndUpdateTokenAndReturnUserUid(String snsAccountUid, String kakaoToken, Integer platform) {
        Optional<SnsLogin> snsLoginOptional = snsLoginRepository.findBySnsAccountUid(snsAccountUid);
        if (snsLoginOptional.isPresent()) { // if already signed up
            SnsLogin snsLoginPresent = snsLoginOptional.get();
            String presentToken = snsLoginPresent.getToken();
            if (!presentToken.contentEquals(kakaoToken)) {
                // update token if varies
                snsLoginPresent.setToken(kakaoToken);
                snsLoginRepository.save(snsLoginPresent);
            }
            return snsLoginPresent.getUserUid();
        } else {
            // insert snsLogin if new signup
            SnsLogin newSnsLogin = new SnsLogin();
            newSnsLogin.setPlatformUid(platform);
            newSnsLogin.setSnsAccountUid(snsAccountUid);
            newSnsLogin.setToken(kakaoToken);
            SnsLogin savedLogin = snsLoginRepository.save(newSnsLogin);
            return savedLogin.getUserUid();
        }
    }

    private void insertNewUserInfoStudentInfo(Integer userUid) {
        UserInfo newUserInfo = new UserInfo();
        StudentInfo newStudentInfo = new StudentInfo();
        newUserInfo.setUserUid(userUid);
        newUserInfo.setHitCount(0);
        newUserInfo.setCreatedDatetime(LocalDateTime.now());
        newStudentInfo.setUserUid(userUid) ;
        System.out.print(newStudentInfo.toString());
        userInfoRepository.save(newUserInfo);
        studentInfoRepository.save(newStudentInfo);
    }

    @Override
    public void userDeactivate(int userUid){
        //비활성화. pp_reg_state to 4
        Optional<PpRegisterState> ppRegisterStateOptional = ppRegisterStateRepository.findByUserUid(userUid);
        PpRegisterState ppRegisterState;
        if(ppRegisterStateOptional.isPresent()){
            ppRegisterState = ppRegisterStateOptional.get();
            ppRegisterState.setStateId(3);
        }
        else{
            ppRegisterState = PpRegisterState.builder()
                    .userUid(userUid)
                    .stateId(3)
                    .build();
        }
        ppRegisterStateRepository.save(ppRegisterState);

        //decrease member_count in pp_rank
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
        if(studentInfoOptional.isPresent()){
            int univUid = studentInfoOptional.get().getUnivUid();
            String studentIdPivot = studentInfoOptional.get().getStudentId();

            Optional<PpRank> ppRankOptional = ppRankRepository.findByUnivUidAndStudentIdPivot(univUid, studentIdPivot);
            PpRank ppRank;
            if(ppRankOptional.isPresent()){
                ppRank = ppRankOptional.get();
                ppRank.setMemberCount(ppRank.getMemberCount()+1);
                ppRankRepository.save(ppRank);
            }
        }
        else{
            //userUid로 user를 찾을 수 없음.
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }

    @Override
    public List<RankVO> getRankList(int userUid, Pageable pageable){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
        if(studentInfoOptional.isPresent()){
            int univUid = studentInfoOptional.get().getUnivUid();

            List<PpRank> ppRanks = ppRankRepository.findAllByUnivUid(univUid, pageable);
            if(!ppRanks.isEmpty()) {
                List<RankVO> rankVOS = ppRanks.stream().map(x -> RankVO.builder()
                                .rankUid(x.getRankUid())
                                .univUid(x.getUnivUid())
                                .studentIdPivot(x.getStudentIdPivot())
                                .memberCount(x.getMemberCount())
                                .build())
                        .sorted(Comparator.comparing(RankVO::getMemberCount).reversed())
                        .collect(Collectors.toList());
                return rankVOS;
            }
            else{
                throw new NotFoundException("RANK_NOT_FOUND");
            }
        }
        else{
            //user_uid로 user를 찾을 수 없음
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }
}
