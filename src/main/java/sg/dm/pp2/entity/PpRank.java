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
@Table(name="pp_rank")
@Entity
@SuperBuilder
public class PpRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rankUid;

    @Column
    private int univUid;

    @Column
    private String studentIdPivot;

    @Column
    private int memberCount;
}
