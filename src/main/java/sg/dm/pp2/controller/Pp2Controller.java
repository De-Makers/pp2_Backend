package sg.dm.pp2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.dm.pp2.chatServer.ChatMessageDTO;
import sg.dm.pp2.chatServer.ChatService;
import sg.dm.pp2.controller.dto.FCMNotificationDTO;
import sg.dm.pp2.controller.dto.PostStudentProfileDTO;
import sg.dm.pp2.controller.dto.TestDTO;
import sg.dm.pp2.service.FCMService;
import sg.dm.pp2.service.S3Upload;
import sg.dm.pp2.service.TokenService;
import sg.dm.pp2.service.email.EmailQueryService;
import sg.dm.pp2.service.text.TextTableService;
import sg.dm.pp2.service.user.PpRegisterStateService;
import sg.dm.pp2.service.user.UserService;
import sg.dm.pp2.service.vo.RegisterStateVO;
import sg.dm.pp2.service.vo.TestVO;
import sg.dm.pp2.service.vo.TextTableVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class Pp2Controller {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailQueryService emailQueryService;
    @Autowired
    private TextTableService textTableService;
    @Autowired
    private PpRegisterStateService ppRegisterStateService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenAuthUtil tokenAuthUtil;

    @PostMapping("/auth/signup/{kakao_uid}/{platform}")
    public void postSignUp(
            @RequestParam("token") String kakaoToken,
            @PathVariable("kakao_uid") String kakaoUid,
            @PathVariable("platform") Integer platform
    ) {
        userService.doSignUp(
                kakaoUid,
                kakaoToken,
                platform
        );
    }

    @GetMapping("/public/text/{text_uid}")
    public TextTableVO getText(@PathVariable(value = "text_Uid") String textUid) {
        return textTableService.getText(textUid);
    }

    @GetMapping("/auth/state")
    public RegisterStateVO getRegisterState(@RequestHeader("Authorization") String token) {
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return ppRegisterStateService.getRegisterState(userUid);
    }


    //TODO : --------------FOR TEST-----------------------
    @Autowired
    FCMService fcmService;

    @PostMapping("/test/message")
    public String sendMessageByToken(@RequestBody FCMNotificationDTO fcmNotificationDTO){
        return fcmService.sendMessageByToken(fcmNotificationDTO);
    }

    @PostMapping("/getname")
    public void getName(@RequestBody TestDTO testDTO) {
        tokenService.testService(testDTO);
    }

    @GetMapping("/test/user-from-token")
    public String getUserUidFromToken(@RequestHeader("Authorization") String token) {
        return tokenService.tokenToUserUidStringService(token.substring(7));
    }
    @PostMapping("/test/auth/signup")
    public String postSignUpAndReturnToken(@RequestHeader Integer userUid) {
        return tokenService.tokenTestService(userUid);
    }

    @Autowired
    ChatService chatService;

    private final SimpMessagingTemplate template;

//    @PostMapping("/chat/message/image")
//    public void messageImage(@RequestBody ChatMessageDTO message){
//        String sessionId = chatService.saveMessageAndReturnSessionId(message);
//        template.convertAndSend("/sub/chat/room/" + sessionId, message);
//    }

    @Autowired
    private S3Upload s3Upload;

    @RequestMapping(value = "/chat/message/image", method = RequestMethod.POST
            ,consumes = {MediaType.APPLICATION_JSON_VALUE ,MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public void chatImageMessage(
            @RequestPart("image") MultipartFile multipartFile,
            @RequestPart("data") ChatMessageDTO message
    ) throws IllegalStateException, IOException {
        String url = s3Upload.upload(multipartFile);
        message.setMessage(url);
        String sessionId = chatService.saveMessageAndReturnSessionId(message);
        template.convertAndSend("/sub/chat/room/" + sessionId, message);
    }


}
