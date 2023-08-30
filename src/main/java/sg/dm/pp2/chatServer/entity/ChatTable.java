package sg.dm.pp2.chatServer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="chat_table")
@Entity
@SuperBuilder
public class ChatTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatUid;

    @Column
    private int chatRoomUid;

    @Column
    private int userUid;

    @Column
    private String message;

    @Column
    private LocalDateTime registeredDatetime;
}
