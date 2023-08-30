package sg.dm.pp2.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.entity.SnsLogin;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.entity.UserInfo;
import sg.dm.pp2.repository.SnsLoginRepository;
import sg.dm.pp2.repository.StudentInfoRepository;
import sg.dm.pp2.repository.UserInfoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    SnsLoginRepository snsLoginRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    StudentInfoRepository studentInfoRepository;

    @Override
    public void doSignUp(
            String snsAccountUid,
            String kakaoToken,
            Integer platform
    ) {
        Integer userUid = checkInsertSnsLoginAndUpdateTokenAndReturnUserUid(snsAccountUid, kakaoToken, platform);
        insertNewUserInfoStudentInfo(userUid);
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
            return savedLogin.getUserUid(); // TODO: 이러면 저장한 userUid return하는지 확인
        }
    }

    private void insertNewUserInfoStudentInfo(Integer userUid) {
        UserInfo newUserInfo = new UserInfo();
        StudentInfo newStudentInfo = new StudentInfo();
        newUserInfo.setUserUid(userUid);
        newUserInfo.setHitCount(0);
        newUserInfo.setCreatedDateTime(LocalDateTime.now());
        newStudentInfo.setUserUid(userUid) ;
        userInfoRepository.save(newUserInfo);
        studentInfoRepository.save(newStudentInfo);
    }
}
