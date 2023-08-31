package sg.dm.pp2.chatServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.chatServer.entity.ChatroomUserTable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatroomUserTable, Integer> {
    List<ChatroomUserTable> findAllByUserUid(int userUid);

    long countByChatroomUid(int chatroomUid);
    List<ChatroomUserTable> findAllByChatroomUid(int chatroomUid);

    Optional<ChatroomUserTable> findByChatroomUidAndUserUid(int chatroomUid, int userUid);

}
