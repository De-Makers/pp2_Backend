package sg.dm.pp2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import sg.dm.pp2.configure.JwtHelper;
import sg.dm.pp2.entity.Test;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.repository.TestRepository;
import sg.dm.pp2.repository.UnivInfoRepository;
import sg.dm.pp2.service.vo.TestVO;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TestServiceImpl implements TestService{
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private UnivInfoRepository univInfoRepository;

    @Autowired
    private JwtHelper jwtHelper;


    @Override
    public TestVO testService(Long id) {
        Optional<Test> test = testRepository.findById(id);
        if (test.isPresent()) {
            return TestVO.builder()
                    .testName(test.get().getTestName())
                    .build();
        } else {
            return TestVO.builder()
                    .testName("not exist")
                    .build();
        }
    }

    @Override
    public String tokenTestService(Integer userUid) {
        return jwtHelper.generateTokenByUserUid(userUid);
    }

    @Override
    public List<UnivInfo> getUnivInfoList() {
        return univInfoRepository.findAll();
    }
}
