package sg.dm.pp2.chatServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.chatServer.entity.ChatroomSessionTable;

import java.util.Optional;

@Repository
public interface ChatRoomSessionRepository extends JpaRepository<ChatroomSessionTable, Integer> {
    Optional<ChatroomSessionTable> findBySessionId(String sessionId);
    Optional<ChatroomSessionTable> findByChatroomUid(int chatroomUid);
}
