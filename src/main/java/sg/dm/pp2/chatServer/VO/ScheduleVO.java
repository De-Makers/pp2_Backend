package sg.dm.pp2.chatServer.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ScheduleVO {
    private int chatroomUid;
    private int userUid;
    private int mon;
    private int tues;
    private int wed;
    private int thur;
    private int fri;
    private int sat;
    private int sun;
}
