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
@Table(name="chatroom_session_table")
@Entity
@SuperBuilder
public class ChatroomSessionTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatroomUid;

    @Column
    private String sessionId;
}
