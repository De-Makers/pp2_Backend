package sg.dm.pp2.service;

import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.service.vo.TestVO;

import java.util.List;

public interface TestService {
    TestVO testService(Long id);

    String tokenToUserUidTestService(String token);

    String tokenTestService(Integer userUid);

    List<UnivInfo> getUnivInfoList();
}
