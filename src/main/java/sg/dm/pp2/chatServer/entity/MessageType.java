package sg.dm.pp2.chatServer.entity;

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
@Table(name="message_type")
@Entity
@SuperBuilder
public class MessageType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int typeUid;

    @Column
    private String messageType;
}
