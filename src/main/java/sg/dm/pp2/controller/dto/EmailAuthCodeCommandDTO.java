package sg.dm.pp2.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailAuthCodeCommandDTO {
    private String email;
    private int univUid;
    private int domainUid;
}
