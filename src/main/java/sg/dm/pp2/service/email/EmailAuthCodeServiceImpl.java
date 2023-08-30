package sg.dm.pp2.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sg.dm.pp2.controller.dto.EmailAuthCodeCommandDTO;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.entity.UnivEmailDomain;
import sg.dm.pp2.exception.DomainException;
import sg.dm.pp2.exception.NotFoundException;
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
    private static final String senderEmail = "";  //보내는 사람 이메일
    private static int number;

    public static void createNumber() {
        number = (int) (Math.random() * (899999)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage CreateMail(String mail) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("뻔뻔 : 학교 이메일 인증");
            String msgg = "";
            msgg += "<div style='margin:100px;'>";
            msgg += "<h1> 안녕하세요 뻔뻔입니다</h1>";
            msgg += "<br>";
            msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
            msgg += "<br>";
            msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
            msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
            msgg += "<div style='font-size:130%'>";
            msgg += "CODE : <strong>";
            msgg += number + "</strong><div><br/> "; // 메일에 인증번호 넣기
            msgg += "</div>";
            message.setText(msgg, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    @Override
    public void sendMail(EmailAuthCodeCommandDTO emailAuthCodeCommandDTO, int userUid) {
        String email = emailAuthCodeCommandDTO.getEmail();
        String parsedDomain = "";
        int count = email.indexOf('@');

        if (count != -1) {
            parsedDomain = email.substring(count + 1);
            Optional<UnivEmailDomain> domainOptional = univEmailDomainRepository.findByDomainUid(emailAuthCodeCommandDTO.getDomainUid());
            Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);

            if (domainOptional.isPresent()) {
                String domain = domainOptional.get().getDomain();
                if (Objects.equals(parsedDomain, domain)) {
                    MimeMessage message = CreateMail(emailAuthCodeCommandDTO.getEmail());
                    StudentInfo studentInfo;
                    if(studentInfoOptional.isPresent()){
                        studentInfo = studentInfoOptional.get();
                        studentInfo.setAuthCode(String.valueOf(number));
                        studentInfo.setStudentEmail(email);
                        studentInfo.setUnivUid(emailAuthCodeCommandDTO.getUnivUid());
                        studentInfoRepository.save(studentInfo);
                        javaMailSender.send(message);
                    }
                    else{
                        throw new DomainException("user_uid로 DB에서 student를 찾지 못함");
                    }

                } else {
                    //(domain_uid로 찾은)디비에 저장된 domain과 맞지 않음
                    throw new DomainException("(domain_uid로 찾은)디비에 저장된 domain과 맞지 않음");
                }

            } else {
                //domain_uid로 domain을 찾지 못함
                throw new NotFoundException("DOMAIN_NOT_FOUND");
            }
        } else {
            //이메일 양식 오류(이메일에 @가 없음)
            throw new DomainException("이메일 양식 오류(이메일에 @가 없음)");
        }
    }

    @Override
    public void checkAuthCode(String code, int userUid){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(userUid);

        if(studentInfoOptional.isPresent()){
            String answerCode = studentInfoOptional.get().getAuthCode();
            if(Objects.equals(answerCode, code)) {
                log.info("AuthCode Success");
            }
            else{
                //인증코드가 맞지 않음
//                log.info("Fail1");
                throw new NotFoundException("WRONG_AUTH_CODE");

            }
        }
        else{
            //user_uid로 student 찾을 수 없음. UserDetailsServiceImpl에서 USER NOT FOUND로 앞에서 걸러짐
//            log.info("Fail2");
            throw new NotFoundException("USER_ID_NOT_FOUND");
        }

    }
}
