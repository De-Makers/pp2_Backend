package sg.dm.pp2.service;

import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.service.vo.TestVO;

import java.util.List;

public interface TokenService {
    TestVO testService(Long id);

    String tokenToUserUidStringService(String token);

    String tokenTestService(Integer userUid);

    List<UnivInfo> getUnivInfoList();
}
