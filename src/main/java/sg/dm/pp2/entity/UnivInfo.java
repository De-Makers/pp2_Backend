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
@Table(name="univ_info")
@Entity
@SuperBuilder
public class UnivInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int univUid;

    @Column
    String univName;
}
