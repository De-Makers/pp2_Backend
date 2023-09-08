package sg.dm.pp2.chatServer.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {
    private int mon;
    private int tues;
    private int wed;
    private int thur;
    private int fri;
    private int sat;
    private int sun;
}
