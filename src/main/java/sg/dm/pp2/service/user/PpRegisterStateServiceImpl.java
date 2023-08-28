package sg.dm.pp2.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.entity.PpRegisterState;
import sg.dm.pp2.exception.NotFoundException;
import sg.dm.pp2.repository.PpRegisterStateRepository;
import sg.dm.pp2.service.vo.RegisterStateVO;

import java.util.Optional;

@Service
@Slf4j
public class PpRegisterStateServiceImpl implements PpRegisterStateService{
    @Autowired
    PpRegisterStateRepository ppRegisterStateRepository;

    @Override
    public RegisterStateVO getRegisterState(int userUid){
        Optional<PpRegisterState> ppRegisterStateOptional = ppRegisterStateRepository.findByUserUid(userUid);
        if(ppRegisterStateOptional.isPresent()){
            int state = ppRegisterStateOptional.get().getStateId();
            RegisterStateVO registerStateVO = RegisterStateVO.builder()
                    .stateId(state)
                    .build();
            return registerStateVO;
        }
        else{
            //user_uid가 pp_reg_state 테이블에 없음
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }
}
