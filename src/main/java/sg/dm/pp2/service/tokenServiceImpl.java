package sg.dm.pp2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.configure.JwtHelper;
import sg.dm.pp2.entity.Test;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.repository.TestRepository;
import sg.dm.pp2.repository.UnivInfoRepository;
import sg.dm.pp2.service.vo.TestVO;
import sg.dm.pp2.service.vo.UnivEmailDomainDetailVO;
import sg.dm.pp2.service.vo.UnivInfoVO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class tokenServiceImpl implements TokenService {
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
    public String tokenToUserUidStringService(String token) {
        return jwtHelper.getUserUidFromToken(token);
    }

    @Override
    public List<UnivInfoVO> getUnivInfoList() {
        List<UnivInfo> univInfoList = univInfoRepository.findAll();
        return univInfoList.stream().map(x -> UnivInfoVO.builder()
                        .univUid(x.getUnivUid())
                        .univName(x.getUnivName())
                        .build())
                .toList();
    }
}
