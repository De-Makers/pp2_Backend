package sg.dm.pp2.chatServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.dm.pp2.chatServer.entity.ChatTable;

@Repository
public interface ChatRepository extends JpaRepository<ChatTable, Integer> {

}
