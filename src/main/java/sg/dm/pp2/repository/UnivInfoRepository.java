package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.entity.UnivInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnivInfoRepository extends JpaRepository<UnivInfo, Integer> {
    List<UnivInfo> findAll();

    Optional<UnivInfo> findByUnivUid(int univUid);
}
