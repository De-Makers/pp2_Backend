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
@Table(name="chatroom_uesr_table")
@Entity
@SuperBuilder
public class ChatroomUserTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Uid;

    @Column
    private int chatroomUid;

    @Column
    private int userUid;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean readCheck;
}
