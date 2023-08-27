package sg.dm.pp2.entity;

import jakarta.annotation.Nullable;
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
@Table(name="pp_reg_state")
@Entity
@SuperBuilder
public class PpRegisterState {
    @Id
    private int userUid;

    @Column
    private int stateId;
}
