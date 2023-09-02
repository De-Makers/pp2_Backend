package sg.dm.pp2.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sg.dm.pp2.controller.dto.FCMNotificationDTO;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.StudentInfoRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class FCMService {
    private final FirebaseMessaging firebaseMessaging;
    private final StudentInfoRepository studentInfoRepository;

    public String sendMessageByToken(FCMNotificationDTO fcmNotificationDTO){
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByUserUid(fcmNotificationDTO.getTargetUserUid());

        if(studentInfoOptional.isPresent()) {
            //if(studentInfoOptional.get().getFBToken() != null){
            Notification notification = Notification.builder()
                    .setTitle(fcmNotificationDTO.getTitle())
                    .setBody(fcmNotificationDTO.getBody())
                    .build();

            Message message = Message.builder()
                    //.setToken(studentInfoOptional.get().getFBToken())
                    .setNotification(notification)
                    .build();
            //}

            try{
                firebaseMessaging.send(message);
                return "메세지 전송 성공. TargetId : " + fcmNotificationDTO.getTargetUserUid();
            } catch (FirebaseMessagingException e){
                e.printStackTrace();
                return "메세지 전송 실패... TargetId : " + fcmNotificationDTO.getTargetUserUid();
            }
        }
        else{
            //타켓 유저를 찾을 수 없음
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }
}
