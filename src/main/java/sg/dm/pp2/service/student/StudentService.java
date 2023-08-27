package sg.dm.pp2.service.student;

import org.springframework.web.bind.annotation.RequestParam;

public interface StudentService {
    void saveFirstProfileForStudentInfo(
            Integer userUid,
            String studentId,
            String name,
            String message
    );
}
