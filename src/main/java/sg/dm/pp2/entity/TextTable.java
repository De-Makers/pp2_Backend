package sg.dm.pp2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="text_table")
@Entity
@SuperBuilder
public class TextTable {
    @Id
    private String textUid;

    @Column(columnDefinition="TEXT")
    private String contents;
}
