package sg.dm.pp2.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.controller.dto.GetUnivEmailDomainQueryDTO;
import sg.dm.pp2.entity.UnivEmailDomain;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.UnivEmailDomainRepository;
import sg.dm.pp2.repository.UnivInfoRepository;
import sg.dm.pp2.service.vo.UnivEmailDomainDetailVO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmailQueryServiceImpl implements EmailQueryService{

    @Autowired
    private UnivEmailDomainRepository univEmailDomainRepository;
    @Autowired
    private UnivInfoRepository univInfoRepository;

    @Override
    public List<UnivEmailDomainDetailVO> getUnivEmailDomainList(int univUid) {

        Optional<UnivInfo> univInfoOptional = univInfoRepository.findByUnivUid(univUid);

        if (univInfoOptional.isPresent()) {

            List<UnivEmailDomain> domains = univEmailDomainRepository.findAllByUnivUid(univUid);

            List<UnivEmailDomainDetailVO> univEmailDomainDetailVO = domains.stream().map(x -> UnivEmailDomainDetailVO.builder()
                            .domainUid(x.getDomainUid())
                            .domain(x.getDomain())
                            .build())
                    .collect(Collectors.toList());


            return univEmailDomainDetailVO;
        } else {
            //univ_uid로 univ를 찾지 못함
            throw new NotFoundException("UNIV_NOT_FOUND");
        }
    }




}
