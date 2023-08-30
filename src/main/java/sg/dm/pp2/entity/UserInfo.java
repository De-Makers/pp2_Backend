package sg.dm.pp2.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="user_info")
@Entity
@SuperBuilder
public class UserInfo {
    @Id
    private int userUid;

    @Column
    private int hitCount;

    @Column(nullable = true)
    private LocalDate expiredDate;

    @Column(nullable = true)
    private LocalDateTime lastLoginDateTime;

    @Column(nullable = true)
    private LocalDateTime modifiedDateTime;

    @Column(nullable = true)
    private LocalDateTime createdDateTime;
}
