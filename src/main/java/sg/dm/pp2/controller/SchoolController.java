package sg.dm.pp2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.controller.dto.GetUnivEmailDomainQueryDTO;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.service.TokenService;
import sg.dm.pp2.service.email.EmailAuthCodeService;
import sg.dm.pp2.service.email.EmailQueryService;
import sg.dm.pp2.service.vo.UnivEmailDomainDetailVO;
import sg.dm.pp2.service.vo.UnivInfoVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SchoolController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailQueryService emailQueryService;
    @Autowired
    private TokenAuthUtil tokenAuthUtil;
    @Autowired
    private EmailAuthCodeService emailAuthCodeService;


    @GetMapping("/pp/school")
    public List<UnivInfoVO> getSchoolList() {
        return tokenService.getUnivInfoList();
    }

    @GetMapping("/pp/school/email-domain/{univ_uid}")
    public List<UnivEmailDomainDetailVO> getUnivEmailDomain(@PathVariable(value = "univ_uid") int univUid){
        return emailQueryService.getUnivEmailDomainList(univUid);
    }

    @PostMapping("/pp/school/email/auth")
    public void checkEmailAuthCode(@RequestHeader ("Authorization") String token, @RequestParam("code") String code){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        emailAuthCodeService.checkAuthCode(code, userUid);
    }



}
