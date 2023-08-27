package sg.dm.pp2.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.entity.StudentInfo;

@Repository
public interface StudentInfoRepository extends JpaRepository<StudentInfo, Integer> {
    public Integer getUnivUidByUserUid(
            Integer userUid
    );

    @Modifying
    @Query("UPDATE StudentInfo s " +
            "SET s.studentId = :studentId, " +
            "s.studentIdYear = :studentIdYear, " +
            "s.studentIdPivot = :studentIdPivot, " +
            "s.name = :name, " +
            "s.message = :message " +
            "WHERE s.userUid = :userUid")
    public void updateStudentInfoByUserUid(
            Integer userUid,
            String studentId,
            Integer studentIdYear,
            String studentIdPivot,
            String name,
            String message
    );
}
