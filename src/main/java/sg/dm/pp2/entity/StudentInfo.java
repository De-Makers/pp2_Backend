package sg.dm.pp2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userUid;

    @Column
    private int univUid;

    @Column
    private String name;

    @Column
    private String studentId;

    @Column
    private int studentIdYear;

    @Column
    private String studentIdPivot;

    @Column
    private String studentEmail;

    @Column
    private String message;

    @Column
    private String authCode;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean certified;

    @Column
    private String studentCardImage;
}
