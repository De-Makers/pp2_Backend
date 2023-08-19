package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.entity.UnivEmailDomain;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnivEmailDomainRepository extends JpaRepository<UnivEmailDomain, Integer> {
    List<UnivEmailDomain> findAllByUnivUid(int univUid);

    Optional<UnivEmailDomain> findByDomainUid(int domainUid);
}
