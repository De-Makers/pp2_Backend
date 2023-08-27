package sg.dm.pp2.service.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.dm.pp2.util.StudentIdUtil;
import sg.dm.pp2.repository.StudentInfoRepository;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentInfoRepository studentInfoRepository;

    @Autowired
    StudentIdUtil studentIdUtil;

    @Override
    public void saveFirstProfileForStudentInfo(
            Integer userUid,
            String studentId,
            String name,
            String message
    ) {
        Integer univUid = studentInfoRepository.getUnivUidByUserUid(userUid);
        StudentIdUtil.YearAndPivot yearAndPivot = studentIdUtil.getYearAndPivotFromStudentIdAndUnivUid(studentId, univUid);
        String studentIdPivot = yearAndPivot.getPivot();
        Integer studentIdYear = yearAndPivot.getYear();
        studentInfoRepository.updateStudentInfoByUserUid(
                userUid = userUid,
                studentId = studentId,
                studentIdYear = studentIdYear,
                studentIdPivot = studentIdPivot,
                name = name,
                message = message
        );
    }
}
