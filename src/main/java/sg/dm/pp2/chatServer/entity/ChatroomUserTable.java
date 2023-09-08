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
@Table(name="chatroom_user_table")
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

    @Column(nullable = true)
    private int mon;

    @Column(nullable = true)
    private int tues;

    @Column(nullable = true)
    private int wed;

    @Column(nullable = true)
    private int thur;

    @Column(nullable = true)
    private int fri;

    @Column(nullable = true)
    private int sat;

    @Column(nullable = true)
    private int sun;
}
