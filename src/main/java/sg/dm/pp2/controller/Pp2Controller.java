package sg.dm.pp2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sg.dm.pp2.controller.dto.TestDTO;
import sg.dm.pp2.service.TestService;
import sg.dm.pp2.service.vo.TestVO;

@RestController
@RequiredArgsConstructor
public class Pp2Controller {
    @Autowired
    private TestService testService;

    @PostMapping("/getname")
    public TestVO getName(@RequestBody TestDTO testDTO){
        return testService.testService(Long.parseLong(testDTO.getId()));
    }
}
