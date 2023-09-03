package sg.dm.pp2.service.email;

import com.google.gson.JsonObject;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sg.dm.pp2.controller.dto.EmailAuthCodeCommandDTO;
import sg.dm.pp2.entity.PpRegisterState;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.entity.UnivEmailDomain;
import sg.dm.pp2.exception.DomainException;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.PpRegisterStateRepository;
import sg.dm.pp2.repository.StudentInfoRepository;
import sg.dm.pp2.repository.UnivEmailDomainRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class EmailAuthCodeServiceImpl implements EmailAuthCodeService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UnivEmailDomainRepository univEmailDomainRepository;
    @Autowired
    private StudentInfoRepository studentInfoRepository;
    @Autowired
    private PpRegisterStateRepository ppRegisterStateRepository;

    @Value("${pp2.mail.url}")
    private String uri;
    @Value("${pp2.mail.x-api-key}")
    private String apiKey;
//    private static final String senderEmail = "";  //보내는 사람 이메일
    private static int number;

    public static void createNumber() {
        number = (int) (Math.random() * (899999)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }
//
//    public MimeMessage CreateMail(String mail) {
//        createNumber();
//        MimeMessage message = javaMailSender.createMimeMessage();
//
//        try {
//
//            message.setFrom(senderEmail);
//            message.setRecipients(MimeMessage.RecipientType.TO, mail);
//            message.setSubject("뻔뻔 : 학교 이메일 인증");
//            String msgg = "";
//            msgg += "<div style='margin:100px;'>";
//            msgg += "<h1> 안녕하세요 뻔뻔입니다</h1>";
//            msgg += "<br>";
//            msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
//            msgg += "<br>";
//            msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
//            msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
//            msgg += "<div style='font-size:130%'>";
//            msgg += "CODE : <strong>";
//            msgg += number + "</strong><div><br/> "; // 메일에 인증번호 넣기
//            msgg += "</div>";
//            message.setText(msgg, "UTF-8", "html");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//        return message;
//    }
//
//    @Override
//    public void sendMail(EmailAuthCodeCommandDTO emailAuthCodeCommandDTO, int userUid) {
//        String email = emailAuthCodeCommandDTO.getEmail();
//        String parsedDomain = "";
//        int count = email.indexOf('@');
//
//        if (count != -1) {
//            parsedDomain = email.substring(count + 1);
//            Optional<UnivEmailDomain> domainOptional = univEmailDomainRepository.findByDomainUid(emailAuthCodeCommandDTO.getDomainUid());
//            Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
//
//            if (domainOptional.isPresent()) {
//                String domain = domainOptional.get().getDomain();
//                if (Objects.equals(parsedDomain, domain)) {
//                    MimeMessage message = CreateMail(emailAuthCodeCommandDTO.getEmail());
//                    StudentInfo studentInfo;
//                    if(studentInfoOptional.isPresent()){
//                        studentInfo = studentInfoOptional.get();
//                        studentInfo.setAuthCode(String.valueOf(number));
//                        studentInfo.setStudentEmail(email);
//                        studentInfo.setUnivUid(emailAuthCodeCommandDTO.getUnivUid());
//                        studentInfoRepository.save(studentInfo);
//                        javaMailSender.send(message);
//                    }
//                    else{
//                        throw new DomainException("user_uid로 DB에서 student를 찾지 못함");
//                    }
//
//                } else {
//                    //(domain_uid로 찾은)디비에 저장된 domain과 맞지 않음
//                    throw new DomainException("(domain_uid로 찾은)디비에 저장된 domain과 맞지 않음");
//                }
//
//            } else {
//                //domain_uid로 domain을 찾지 못함
//                throw new NotFoundException("DOMAIN_NOT_FOUND");
//            }
//        } else {
//            //이메일 양식 오류(이메일에 @가 없음)
//            throw new DomainException("이메일 양식 오류(이메일에 @가 없음)");
//        }
//    }


    @Override
    public void sendMail(EmailAuthCodeCommandDTO emailAuthCodeCommandDTO, int userUid) {

        String email = emailAuthCodeCommandDTO.getEmail();
        String parsedDomain = "";
        int count = email.indexOf('@');

        if (count != -1) {
            parsedDomain = email.substring(count + 1);
            log.info("parsed : " + parsedDomain);
            Optional<UnivEmailDomain> domainOptional = univEmailDomainRepository.findByDomainUid(emailAuthCodeCommandDTO.getDomainUid());
            Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);

            if (domainOptional.isPresent()) {
                String domain = domainOptional.get().getDomain();
                if (Objects.equals(parsedDomain, domain)) {
                    StudentInfo studentInfo;
                    if(studentInfoOptional.isPresent()){
                        //외부 api에 post 요청
                        try {
                            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
                            HttpPost postRequest = new HttpPost(uri); //POST 메소드 URL 새성
                            postRequest.setHeader("Connection", "keep-alive");
                            postRequest.setHeader("Content-Type", "application/json");
                            postRequest.addHeader("x-api-key", apiKey); //KEY 입력
                            //postRequest.setHeader("Accept", "application/json");
                            //postRequest.addHeader("Authorization", token); // token 이용시

                            //json body 생성
                            createNumber();
                            String codeString = Integer.toString(number);
                            log.info("codeString : " + codeString);
                            JsonObject param = new JsonObject();
                            param.addProperty("email", email);
                            param.addProperty("code", codeString);

                            StringEntity params = new StringEntity(param.toString());
                            postRequest.setEntity(params);
                            HttpResponse response = client.execute(postRequest);

                            //Response 출력
                            if (response.getStatusLine().getStatusCode() == 200) {
                                ResponseHandler<String> handler = new BasicResponseHandler();
                                String body = handler.handleResponse(response);
                                System.out.println(body);
                            } else {
                                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                            }
                        } catch (Exception e){
                            System.err.println(e.toString());
                        }

                        //저장
                        studentInfo = studentInfoOptional.get();
                        studentInfo.setAuthCode(String.valueOf(number));
                        log.info("authCode : " + String.valueOf(number));
                        studentInfo.setStudentEmail(email);
                        studentInfo.setUnivUid(emailAuthCodeCommandDTO.getUnivUid());
                        studentInfoRepository.save(studentInfo);
                    }
                    else{
                        //user_uid로 DB에서 student를 찾지 못함
                        log.info("1");
                        throw new NotFoundException("USER_NOT_FOUND");
                    }

                } else {
                    //(domain_uid로 찾은)디비에 저장된 domain과 맞지 않음
                    log.info("2");
                    throw new NotFoundException("DOMAIN_NOT_FOUND");
                }

            } else {
                //domain_uid로 domain을 찾지 못함
                log.info("3");
                throw new NotFoundException("DOMAIN_NOT_FOUND");
            }
        } else {
            //이메일 양식 오류(이메일에 @가 없음)
            log.info("4");
            throw new NotFoundException("NOT_FOUND_@");
        }

    }


    @Override
    public void checkAuthCode(String code, int userUid){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);
        Optional<PpRegisterState> ppRegisterStateOptional = ppRegisterStateRepository.findByUserUid(userUid);

        if(studentInfoOptional.isPresent()){
            String answerCode = studentInfoOptional.get().getAuthCode();
            log.info("code :" +code);
            log.info("answerCode : " + answerCode);
            if(Objects.equals(answerCode, code)) {
                log.info("AuthCode Success");
                PpRegisterState ppRegisterState;
                if(ppRegisterStateOptional.isPresent()){
                    ppRegisterState = ppRegisterStateOptional.get();
                    ppRegisterState.setStateId(1);
                }
                else{
                    ppRegisterState = PpRegisterState.builder()
                            .userUid(userUid)
                            .stateId(1)
                            .build();
                }
                ppRegisterStateRepository.save(ppRegisterState);

            }
            else{
                //인증코드가 맞지 않음
                log.info("Fail1");
                throw new NotFoundException("WRONG_AUTH_CODE");

            }
        }
        else{
            //user_uid로 student 찾을 수 없음. UserDetailsServiceImpl에서 USER NOT FOUND로 앞에서 걸러짐
            log.info("Fail2");
            throw new NotFoundException("USER_ID_NOT_FOUND");
        }

    }
}
