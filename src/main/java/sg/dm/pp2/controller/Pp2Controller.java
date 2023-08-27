package sg.dm.pp2.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.controller.dto.GetUnivEmailDomainQueryDTO;
import sg.dm.pp2.controller.dto.TestDTO;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.service.TokenService;
import sg.dm.pp2.service.email.EmailQueryService;
import sg.dm.pp2.service.vo.TestVO;
import sg.dm.pp2.service.vo.UnivEmailDomainDetailVO;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Pp2Controller {
    @Autowired
    private TokenService tokenService;
    @Autowired EmailQueryService emailQueryService;

    @PostMapping("/getname")
    public TestVO getName(@RequestBody TestDTO testDTO){
        return tokenService.testService(Long.parseLong(testDTO.getId()));
    }

    @GetMapping("/pp/school")
    public List<UnivInfo> getSchoolList() {
        return tokenService.getUnivInfoList();
    }

    @GetMapping("/pp/school/email-domain")
    public List<UnivEmailDomainDetailVO> getUnivEmailDomain(@ModelAttribute GetUnivEmailDomainQueryDTO getUnivEmailDomainQueryDTO){
        return emailQueryService.getUnivEmailDomainList(getUnivEmailDomainQueryDTO);
    }

    @PostMapping("/auth/signup")
    public String postSignUpAndReturnToken(@RequestHeader Integer userUid) {
        return tokenService.tokenTestService(userUid);
    }

    @GetMapping("/test/user-from-token")
    public String getUserUidFromToken(@RequestHeader("Authorization") String token) {
        return tokenService.tokenToUserUidStringService(token.substring(7));
    }
}
