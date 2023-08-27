package sg.dm.pp2.service.user;

import sg.dm.pp2.service.vo.RegisterStateVO;

public interface PpRegisterStateService {
    RegisterStateVO getRegisterState(int userUid);
}
