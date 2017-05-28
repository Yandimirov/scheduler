package ru.scheduler.users.service;

import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;

import java.util.List;


public interface UserService {
    User getUserById(Long id);

    void addUser(User user);

    User findUserByEmail(String email);

    User findUserByUsername(String username);

    User save(User user);

    User update(User user, AuthDTO authDTO);

    UserRole getRole(Long id);

    List<User> getAll();

    List<User> fidnAllByEmailNot(String email);
}
