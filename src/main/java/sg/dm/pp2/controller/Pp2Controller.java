package sg.dm.pp2.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.controller.dto.GetUnivEmailDomainQueryDTO;
import sg.dm.pp2.controller.dto.TestDTO;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.service.TestService;
import sg.dm.pp2.service.email.EmailQueryService;
import sg.dm.pp2.service.vo.TestVO;
import sg.dm.pp2.service.vo.UnivEmailDomainDetailVO;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Pp2Controller {
    @Autowired
    private TestService testService;
    @Autowired EmailQueryService emailQueryService;

    @PostMapping("/getname")
    public TestVO getName(@RequestBody TestDTO testDTO){
        return testService.testService(Long.parseLong(testDTO.getId()));
    }

    @GetMapping("/pp/school")
    public List<UnivInfo> getSchoolList() {
        return testService.getUnivInfoList();
    }

    @GetMapping("/pp/school/email-domain")
    public List<UnivEmailDomainDetailVO> getUnivEmailDomain(@ModelAttribute GetUnivEmailDomainQueryDTO getUnivEmailDomainQueryDTO){
        return emailQueryService.getUnivEmailDomainList(getUnivEmailDomainQueryDTO);
    }

    @PostMapping("/auth/signup")
    public String postSignUpAndReturnToken(@RequestHeader Integer userUid) {
        return testService.tokenTestService(userUid);
    }

    @GetMapping("/test/user-from-token")
    public String getUserUidFromToken(@RequestHeader("Authorization") String token) {
        return testService.tokenToUserUidTestService(token.substring(7));
    }
}
