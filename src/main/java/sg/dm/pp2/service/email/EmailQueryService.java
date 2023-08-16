package sg.dm.pp2.service.email;

import sg.dm.pp2.controller.dto.GetUnivEmailDomainQueryDTO;
import sg.dm.pp2.service.vo.UnivEmailDomainDetailVO;

import java.util.List;

public interface EmailQueryService {
    List<UnivEmailDomainDetailVO> getUnivEmailDomainList(GetUnivEmailDomainQueryDTO  getUnivEmailDomainQueryDTO);
}
