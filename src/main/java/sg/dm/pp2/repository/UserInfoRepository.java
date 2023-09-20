package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.entity.StudentInfo;
import sg.dm.pp2.entity.UnivInfo;
import sg.dm.pp2.entity.UserInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    List<UserInfo> findAll();
    Optional<UserInfo> findByUserUid(Integer userUid);
}
