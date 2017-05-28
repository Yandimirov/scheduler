package ru.scheduler.messages.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.scheduler.messages.model.entity.Chat;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.model.entity.UserChat;

import java.util.List;

public interface UserChatRepository extends CrudRepository<UserChat, Long> {
    List<UserChat> findByUser(User user);

    List<UserChat> findByChatAndUserNot(Chat chat, User user);

    UserChat findByChatAndUser(Chat chat, User user);

    @Query(value = "select uc.* from user_chats uc left join (select m.chat_id ,max(time_stamp) time_stamp from messages m group by m.chat_id ) m on(uc.chat_id = m.chat_id) where uc.user_id = ?1 order by m.time_stamp desc", nativeQuery = true)
    List<UserChat> findByUserAndOrderByLastMessage(Long userId);

    @Query(value = "select count(*) from user_chats where chat_id =?1", nativeQuery = true)
    Long countByUsersInChat(Long id);

    @Query(value = "select t1.chat_id from user_chats t1 left join user_chats t2 on t1.chat_id = t2.chat_id where t1.chat_id in( select chat_id from user_chats group by chat_id having count(chat_id) = 2 ) and t1.user_id = ?1 and t2.user_id = ?2", nativeQuery = true)
    Long findByUsersAndCountUsersEqualTwo(Long userId1, Long userId2);
}
