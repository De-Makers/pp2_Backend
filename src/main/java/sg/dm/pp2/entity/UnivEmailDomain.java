package sg.dm.pp2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="univ_email_domain")
@Entity
@SuperBuilder
public class UnivEmailDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int domainUid;

    @Column
    private int univUid;

    @Column
    private String domain;
}
