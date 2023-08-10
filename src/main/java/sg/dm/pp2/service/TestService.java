package sg.dm.pp2.service;

import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.service.vo.TestVO;

import java.util.List;

public interface TestService {
    TestVO testService(Long id);

    List<UnivInfo> getUnivInfoList();
}
