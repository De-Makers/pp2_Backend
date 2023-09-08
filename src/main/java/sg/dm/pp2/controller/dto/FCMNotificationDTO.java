package sg.dm.pp2.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FCMNotificationDTO {
    private int targetUserUid;
    private String title;
    private String body;
}
