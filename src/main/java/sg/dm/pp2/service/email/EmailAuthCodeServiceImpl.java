package sg.dm.pp2.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sg.dm.pp2.controller.dto.EmailAuthCodeCommandDTO;
import sg.dm.pp2.entity.UnivEmailDomain;
import sg.dm.pp2.repository.UnivEmailDomainRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class EmailAuthCodeServiceImpl implements EmailAuthCodeService{

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UnivEmailDomainRepository univEmailDomainRepository;
    private static final String senderEmail = "";  //보내는 사람 이메일
    private static int number;

    public static void createNumber(){
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage CreateMail(String mail){
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
            message.setText(msgg,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    @Override
    public void sendMail(EmailAuthCodeCommandDTO emailAuthCodeCommandDTO){
        String email = emailAuthCodeCommandDTO.getEmail();
        String parsedDomain = "";
        boolean findFlag = true;
        int count=0;

        while(email.charAt(count) != '@'){
            if(count > email.length()){
                findFlag = false;
                break;
            }
            count++;
        }

        if(findFlag){
            parsedDomain = email.substring(count+1);
            Optional<UnivEmailDomain> domainOptional = univEmailDomainRepository.findByDomainUid(emailAuthCodeCommandDTO.getDomainUid());

            if(domainOptional.isPresent()){
                String domain = domainOptional.get().getDomain();
                if(Objects.equals(parsedDomain, domain)){
                    MimeMessage message = CreateMail(emailAuthCodeCommandDTO.getEmail());
                    javaMailSender.send(message);
                }
                else{
                    //(domain_uid로 찾은)디비에 저장된 domain과 맞지 않음
                }

            }
            else{
                //domain_uid로 domain을 찾지 못함
            }
        }
        else{
            //이메일 양식 오류(이메일에 @가 없음)
        }



    }
}
