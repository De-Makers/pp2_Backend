package sg.dm.pp2.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sg.dm.pp2.entity.SnsLogin;

import java.util.Optional;

public interface SnsLoginRepository extends JpaRepository<SnsLogin, Long> {
    Optional<SnsLogin> findByUserUid(Integer userUid);

    Optional<SnsLogin> findBySnsAccountUid(String snsAccountUid);
}