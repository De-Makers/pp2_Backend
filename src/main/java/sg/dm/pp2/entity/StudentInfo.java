package sg.dm.pp2.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="student_info")
@Entity
@SuperBuilder
public class StudentInfo {
    @Id
    private int userUid;

    @Column(nullable = true)
    private Integer univUid;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String studentId;

    @Column(nullable = true)
    private Integer studentIdYear;

    @Column(nullable = true)
    private String studentIdPivot;

    @Column(nullable = true)
    private String studentEmail;

    @Column(nullable = true)
    private String message;

    @Column(nullable = true)
    private String authCode;

    @Column(nullable = true)
    private String studentCardImage;

    @Column(nullable = true)
    private String profileImageUrl;
}
