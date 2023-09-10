package sg.dm.pp2.service.user;

import org.springframework.data.domain.Pageable;
import sg.dm.pp2.service.vo.JWTVO;
import sg.dm.pp2.service.vo.RankVO;

import java.util.List;

public interface UserService {
    JWTVO doSignUp(
            long snsAccountUid,
            String token,
            Integer platformUid
    );

//    JWTVO doSignIn(
//            Integer snsAccountUid,
//            String token,
//            Integer platformUid
//    );

    void userDeactivate(int userUid);

    List<RankVO> getRankList(int userUid, Pageable pageable);
}
