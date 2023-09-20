package sg.dm.pp2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.controller.dto.FCMNotificationDTO;
import sg.dm.pp2.controller.dto.SignupTokenDTO;
import sg.dm.pp2.controller.dto.TestDTO;
import sg.dm.pp2.service.FCMService;
import sg.dm.pp2.service.TokenService;
import sg.dm.pp2.service.email.EmailQueryService;
import sg.dm.pp2.service.text.TextTableService;
import sg.dm.pp2.service.user.PpRegisterStateService;
import sg.dm.pp2.service.user.UserService;
import sg.dm.pp2.service.vo.JWTVO;
import sg.dm.pp2.service.vo.RankVO;
import sg.dm.pp2.service.vo.RegisterStateVO;
import sg.dm.pp2.service.vo.TextTableVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.util.List;

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

    @PostMapping("/auth/login/{platform_uid}/{sns_account_uid}")
    public JWTVO postSignUp(
            @RequestBody SignupTokenDTO signupTokenDTO,
            @PathVariable("sns_account_uid") long snsAccountUid,
            @PathVariable("platform_uid") Integer platformUid
    ) {
        return userService.doSignUp(
                snsAccountUid,
                signupTokenDTO.getToken(),
                signupTokenDTO.getFcmToken(),
                platformUid
        );
    }

//    @PostMapping("/auth/signin/{sns_account_uid}/{platform_uid}")
//    public JWTVO postSignIn(
//            @RequestBody SignupTokenDTO signupTokenDTO,
//            @PathVariable("sns_account_uid") Integer snsAccountUid,
//            @PathVariable("platform_uid") Integer platformUid
//    ) {
//        return userService.doSignIn(
//                snsAccountUid,
//                signupTokenDTO.getToken(),
//                platformUid
//        );
//    }

    @GetMapping("/public/text/{text_uid}")
    public TextTableVO getText(@PathVariable(value = "text_Uid") String textUid) {
        return textTableService.getText(textUid);
    }

    @GetMapping("/auth/state")
    public RegisterStateVO getRegisterState(@RequestHeader("Authorization") String token) {
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return ppRegisterStateService.getRegisterState(userUid);
    }

    @PostMapping("/pp/deactivate")
    public void userDeactivate(@RequestHeader("Authorization") String token){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        userService.userDeactivate(userUid);
    }

    @GetMapping("/pp/rank")
    public List<RankVO> getRank(@RequestHeader("Authorization") String token, Pageable pageable){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return userService.getRankList(userUid, pageable);
    }


    //TODO : ----------------FOR TEST-----------------------
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
        return tokenService.tokenTestService(userUid, true);
    }



//    @PostMapping("/chat/message/image")
//    public void messageImage(@RequestBody ChatMessageDTO message){
//        String sessionId = chatService.saveMessageAndReturnSessionId(message);
//        template.convertAndSend("/sub/chat/room/" + sessionId, message);
//    }



}
