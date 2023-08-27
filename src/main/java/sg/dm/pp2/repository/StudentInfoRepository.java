package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.dm.pp2.entity.SnsLogin;
import sg.dm.pp2.entity.StudentInfo;

import java.util.Optional;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Integer> {
    Optional<StudentInfo> findByUserUid(Integer userUid);
}
