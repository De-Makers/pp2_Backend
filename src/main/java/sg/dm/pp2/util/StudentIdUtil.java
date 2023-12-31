package sg.dm.pp2.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class StudentIdUtil {
    public YearAndPivot getYearAndPivotFromStudentIdAndUnivUid(String studentId, Integer univUid) throws RuntimeException {
        YearAndPivot yearAndPivot = new YearAndPivot();
        if (univUid == 1) {
            yearAndPivot.year = studentId.substring(0, 4);
            yearAndPivot.pivot = studentId.substring(4, 8);
        } else {
            throw new RuntimeException("univUid에 해당하는 대학교 정보 없음");
        }
        return yearAndPivot;
    }

    @Getter
    @Setter
    public static class YearAndPivot {
        String year;
        String pivot;
    }
}
