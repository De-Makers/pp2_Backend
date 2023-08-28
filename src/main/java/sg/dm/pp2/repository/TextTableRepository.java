package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.entity.TextTable;

import java.util.Optional;

@Repository
public interface TextTableRepository extends JpaRepository<TextTable, String> {
    Optional<TextTable> findByTextUid(String textUid);
}
