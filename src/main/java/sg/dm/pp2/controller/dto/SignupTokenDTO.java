package sg.dm.pp2.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupTokenDTO {
    private String token;
    private String fcmToken;
}
