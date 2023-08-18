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
@Table(name="sns_login")
@Entity
@SuperBuilder
public class SnsLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int userUid;

    @Column
    int platformUid;

    @Column
    String snsAccountUid;

    @Column
    String token;
}
