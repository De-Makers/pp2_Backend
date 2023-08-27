package sg.dm.pp2.service.text;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.entity.TextTable;
import sg.dm.pp2.exception.UserNotFoundException;
import sg.dm.pp2.repository.TextTableRepository;
import sg.dm.pp2.service.vo.TextTableVO;

import java.util.Optional;

@Service
@Slf4j
public class TextTableServiceImpl implements TextTableService{
    @Autowired
    TextTableRepository textTableRepository;
    @Override
    public TextTableVO getText(String textUid){
        Optional<TextTable> textTableOptional = textTableRepository.findByTextUid(textUid);
        if(textTableOptional.isPresent()){
            String textContents = textTableOptional.get().getContents();
            TextTableVO textTableVO = TextTableVO.builder()
                    .contents(textContents)
                    .build();
            return textTableVO;
        }
        else{
            log.info(textUid);
            throw new UserNotFoundException("TEXT_NOT_FOUND");
        }
    }
}
