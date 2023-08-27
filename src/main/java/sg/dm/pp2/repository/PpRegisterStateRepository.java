package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.entity.PpRegisterState;

import java.util.Optional;

@Repository
public interface PpRegisterStateRepository extends JpaRepository<PpRegisterState, Integer> {
    Optional<PpRegisterState> findByUserUid(int userUid);
}
