package sg.dm.pp2.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sg.dm.pp2.entity.SnsLogin;
import sg.dm.pp2.entity.StudentInfo;

import java.util.List;
import java.util.Optional;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Integer> {
    Optional<StudentInfo> findByUserUid(Integer userUid);
    List<StudentInfo> findAllByUnivUidAndStudentIdPivot(int univUid, String studentIdPivot);
    long countByUnivUidAndStudentIdPivot(int univUid, String studentIdPivot);

    @Transactional
    @Modifying
    @Query("UPDATE StudentInfo s " +
            "SET s.studentId = :studentId, " +
            "s.studentIdYear = :studentIdYear, " +
            "s.studentIdPivot = :studentIdPivot, " +
            "s.name = :name, " +
            "s.message = :message, " +
            "s.profileImageUrl = :url " +
            "WHERE s.userUid = :userUid")
    public void updateStudentInfoByUserUid(
            Integer userUid,
            String studentId,
            String studentIdYear,
            String studentIdPivot,
            String name,
            String message,
            String url
    );
}
