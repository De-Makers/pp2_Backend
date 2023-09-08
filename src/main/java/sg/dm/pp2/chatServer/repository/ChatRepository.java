package sg.dm.pp2.chatServer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.chatServer.entity.ChatTable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatTable, Integer> {
    Optional<ChatTable> findTopByChatroomUidOrderByChatUidDesc(int chatroomUid);
    List<ChatTable> findAllByChatroomUidOrderByChatUidDesc(int chatroomUid, Pageable pageable);
}
