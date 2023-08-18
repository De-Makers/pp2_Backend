package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.dm.pp2.entity.SnsLogin;

import java.util.Optional;

public interface SnsLoginRepository extends JpaRepository<SnsLogin, Long> {
    Optional<SnsLogin> findByUserUid(Integer userUid);
}