package sg.dm.pp2.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.entity.PpRank;

import java.util.List;
import java.util.Optional;

@Repository
public interface PpRankRepository extends JpaRepository<PpRank, Integer> {
    Optional<PpRank> findByUnivUidAndStudentIdPivot(int univUid, String studentIdPivot);

    List<PpRank> findAllByUnivUid(int univUid, Pageable pageable);
}
