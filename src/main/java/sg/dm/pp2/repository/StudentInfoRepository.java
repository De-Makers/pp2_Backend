package sg.dm.pp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.dm.pp2.entity.StudentInfo;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Integer> {

}
