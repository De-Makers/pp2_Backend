package sg.dm.pp2.service.user;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sg.dm.pp2.entity.*;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.*;
import sg.dm.pp2.service.TokenService;
import sg.dm.pp2.service.vo.JWTVO;
import sg.dm.pp2.service.vo.RankVO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

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
    @Autowired
    TokenService tokenService;

    @Value("${pp2.kakao.url}")
    private String uri;


    @Override
    public JWTVO doSignUp(
            long snsAccountUid,
            String token,
            String fcmToken,
            Integer platformUid
    ) {
        Integer userUid = checkInsertSnsLoginAndUpdateTokenAndReturnUserUid(snsAccountUid, token, fcmToken, platformUid);
        if(userUid == -1){
            throw new NotFoundException("CANNOT_SIGNUP");
        }

        String Access = tokenService.tokenTestService(userUid, true);
        String Refresh = tokenService.tokenTestService(userUid, false);
        JWTVO jwtVO = new JWTVO().builder()
                .Authorization(Access)
                .Refresh(Refresh)
                .build();

        return jwtVO;
    }

//    @Override
//    public JWTVO doSignIn(
//            Integer snsAccountUid,
//            String token,
//            Integer platformUid
//    ){
//        String sendToken = "Bearer " + token;
//        log.info("sendToken : " + sendToken);
//
//        try {
//            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
//            HttpGet getRequest = new HttpGet(uri); //POST 메소드 URL 새성
//            getRequest.setHeader("Connection", "keep-alive");
//            getRequest.setHeader("Content-Type", "application/json");
//            getRequest.addHeader("Authorization", sendToken); //KEY 입력
//            //postRequest.setHeader("Accept", "application/json");
//            //postRequest.addHeader("Authorization", token); // token 이용시
//
//            HttpResponse response = client.execute(getRequest);
//
//            //Response 출력
//            if (response.getStatusLine().getStatusCode() == 200) {
//                ResponseHandler<String> handler = new BasicResponseHandler();
//                String body = handler.handleResponse(response);
//                System.out.println(body);
//
//                JSONParser jsonParser = new JSONParser();
//                JSONObject object = (JSONObject) jsonParser.parse(body);
//                long id = (long) object.get("id");
//                log.info("id : " + id);
//                if(id == snsAccountUid){
//                    Optional<SnsLogin> snsLoginOptional = snsLoginRepository.findBySnsAccountUidAndPlatformUid(snsAccountUid, platformUid);
//                    if(snsLoginOptional.isPresent()){
//                        int userUid = snsLoginOptional.get().getUserUid();
//                        String Access = tokenService.tokenTestService(userUid, true);
//                        String Refresh = tokenService.tokenTestService(userUid, false);
//                        JWTVO jwtVO = new JWTVO().builder()
//                                .Authorization(Access)
//                                .Refresh(Refresh)
//                                .build();
//                        return jwtVO;
//                    }
//                    else{
//                        throw new NotFoundException("USER_NOT_FOUND");
//                    }
//                }
//
//            } else {
//                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
//                throw new NotFoundException("WRONG_RESPONSE");
//            }
//        } catch (Exception e){
//            System.err.println(e.toString());
//        }
//        throw new NotFoundException("USER_NOT_FOUND");
//    }

    private Integer checkInsertSnsLoginAndUpdateTokenAndReturnUserUid(long snsAccountUid, String token, String fcmToken, Integer platformUid) {
        //외부 api에 get 요청
        String sendToken = "Bearer " + token;
        log.info("sendToken : " + sendToken);

        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpGet getRequest = new HttpGet(uri); //POST 메소드 URL 새성
            getRequest.setHeader("Connection", "keep-alive");
            getRequest.setHeader("Content-Type", "application/json");
            getRequest.addHeader("Authorization", sendToken); //KEY 입력
            //postRequest.setHeader("Accept", "application/json");
            //postRequest.addHeader("Authorization", token); // token 이용시

            HttpResponse response = client.execute(getRequest);

            //Response 출력
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                System.out.println(body);

                JSONParser jsonParser = new JSONParser();
                JSONObject object = (JSONObject) jsonParser.parse(body);
                long id = (long) object.get("id");
                log.info("id : " + id);
                //토큰 유효성 검사 성공
                if(id == snsAccountUid){
                    Optional<SnsLogin> snsLoginOptional = snsLoginRepository.findBySnsAccountUidAndPlatformUid(snsAccountUid, platformUid);
                    if(snsLoginOptional.isPresent()){ //이미 회원가입이 된 상태면(로그인이면)
                        int userUid = snsLoginOptional.get().getUserUid();
                        Optional<UserInfo> userInfoOptional = userInfoRepository.findByUserUid(userUid);
                        if(userInfoOptional.isPresent()){
                            UserInfo userInfo = userInfoOptional.get();
                            userInfo.setFcmToken(fcmToken);
                            userInfoRepository.save(userInfo);
                        }
                        else{
                            throw new NotFoundException("USER_NOT_FOUND");
                        }
                        return userUid;
                    }
                    else{ //회원가입이면
                        //snsLogin에 저장
                        SnsLogin newSnsLogin = new SnsLogin();
                        newSnsLogin.setPlatformUid(platformUid);
                        newSnsLogin.setSnsAccountUid(snsAccountUid);
                        SnsLogin savedLogin = snsLoginRepository.save(newSnsLogin);
                        int userUid = savedLogin.getUserUid();

                        //user_info와 student_info에 유저 생성
                        insertNewUserInfoStudentInfo(userUid, fcmToken);

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

                        return userUid;
                    }
                }

            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                throw new NotFoundException("WRONG_RESPONSE");
            }
        } catch (Exception e){
            System.err.println(e.toString());
            throw new NotFoundException("CANNOT_SEND_AUTH");
        }
        return -1;

    }

    private void insertNewUserInfoStudentInfo(Integer userUid, String fcmToken) {
        UserInfo newUserInfo = new UserInfo();
        StudentInfo newStudentInfo = new StudentInfo();
        newUserInfo.setUserUid(userUid);
        newUserInfo.setHitCount(0);
        newUserInfo.setFcmToken(fcmToken);
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
