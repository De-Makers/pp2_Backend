package sg.dm.pp2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.configure.JwtHelper;
import sg.dm.pp2.controller.dto.TestDTO;
import sg.dm.pp2.entity.Test;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.repository.TestRepository;
import sg.dm.pp2.repository.UnivInfoRepository;
import sg.dm.pp2.service.vo.TestVO;
import sg.dm.pp2.service.vo.UnivEmailDomainDetailVO;
import sg.dm.pp2.service.vo.UnivInfoVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class tokenServiceImpl implements TokenService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private UnivInfoRepository univInfoRepository;

    @Autowired
    private JwtHelper jwtHelper;


    @Override
    public void testService(TestDTO testDTO) {
        String name = testDTO.getTestName();
        Test test = Test.builder()
                .testName(name)
                .build();
        Test savedTest = testRepository.save(test);
        log.info("Id : " + savedTest.getId());

        Optional<Test> testOptional = testRepository.findByTestName(name);
        if(testOptional.isPresent()){
            int id = testOptional.get().getId();
            log.info("찾을수 있니? : " + id);
        }


    }

    @Override
    public String tokenTestService(Integer userUid, boolean access) {
        if(access == true) {
            return jwtHelper.generateTokenByUserUid(userUid, TokenAuthUtil.ACCESS_JWT_TOKEN_VALIDITY);
        }
        else{
            return jwtHelper.generateTokenByUserUid(userUid, TokenAuthUtil.REFRESH_JWT_TOKEN_VALIDITY);
        }
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
