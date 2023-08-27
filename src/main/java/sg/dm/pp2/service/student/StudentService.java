package sg.dm.pp2.service.student;

import sg.dm.pp2.service.vo.MyProfileVO;
import sg.dm.pp2.service.vo.ProfileListVO;

import java.util.List;

public interface StudentService {
    void saveFirstProfileForStudentInfo(
            Integer userUid,
            String studentId,
            String name,
            String message
    );

    MyProfileVO getMyProfile(int userUid);

    List<ProfileListVO> getFamilyProfileList(int userUid);

}
