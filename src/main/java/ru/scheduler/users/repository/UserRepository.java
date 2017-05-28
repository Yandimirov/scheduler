package ru.scheduler.users.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.scheduler.users.model.entity.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {
    //    User getUserById(long id);
    User findByEmail(String email);
    List<User> findByEmailNot(String email);
    List<User> findByEmailNotIn(Set<String> emails);
    User findByUsername(String username);

    @Query(value = "select * from users where date_part('day', birthday) = ?1 and date_part('month', birthday) = ?2", nativeQuery = true)
    List<User> findByBirthday(int day, int month);
}
