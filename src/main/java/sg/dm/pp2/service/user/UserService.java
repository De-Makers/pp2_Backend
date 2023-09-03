package sg.dm.pp2.service.user;

import org.springframework.data.domain.Pageable;
import sg.dm.pp2.service.vo.RankVO;

import java.util.List;

public interface UserService {
    void doSignUp(
            String snsAccountUid,
            String kakaoToken,
            Integer platform
    );

    void userDeactivate(int userUid);

    List<RankVO> getRankList(int userUid, Pageable pageable);
}
