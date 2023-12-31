package sg.dm.pp2.service.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfileListVO {
    private Integer userUid;
    private String name;
    private String studentId;
    private String studentIdYear;
    private String studentIdPivot;
    private String message;
    private String imgUrl;
    private int chatroomUid;
    private Boolean readCheck;
    private String lastMessage;
}
