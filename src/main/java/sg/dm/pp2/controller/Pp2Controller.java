package sg.dm.pp2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.controller.dto.TestDTO;
import sg.dm.pp2.service.TokenService;
import sg.dm.pp2.service.email.EmailQueryService;
import sg.dm.pp2.service.text.TextTableService;
import sg.dm.pp2.service.user.PpRegisterStateService;
import sg.dm.pp2.service.user.UserService;
import sg.dm.pp2.service.vo.RegisterStateVO;
import sg.dm.pp2.service.vo.TestVO;
import sg.dm.pp2.service.vo.TextTableVO;
import sg.dm.pp2.util.TokenAuthUtil;

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

    @PostMapping("/getname")
    public TestVO getName(@RequestBody TestDTO testDTO){
        return tokenService.testService(Long.parseLong(testDTO.getId()));
    }

    @PostMapping("/auth/signup")
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

    @GetMapping("/test/user-from-token")
    public String getUserUidFromToken(@RequestHeader("Authorization") String token) {
        return tokenService.tokenToUserUidStringService(token.substring(7));
    }

    @GetMapping("/public/text/{text_uid}")
    public TextTableVO getText(@PathVariable(value = "text_Uid") String textUid){
        return textTableService.getText(textUid);
    }

    @GetMapping("/auth/state")
    public RegisterStateVO getRegisterState(@RequestHeader ("Authorization") String token) {
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return ppRegisterStateService.getRegisterState(userUid);
    }
}
