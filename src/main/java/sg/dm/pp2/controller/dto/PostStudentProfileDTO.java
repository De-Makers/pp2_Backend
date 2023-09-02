package sg.dm.pp2.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostStudentProfileDTO {
    private String studentId;
    private String name;
    private String message;
}
