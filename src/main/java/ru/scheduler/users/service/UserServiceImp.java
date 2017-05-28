package ru.scheduler.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;
import ru.scheduler.users.repository.UserRepository;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.text.ParseException;
import java.util.*;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @Override
    public UserRole getRole(Long id){
        User user = userRepository.findOne(id);
        return user.getRole();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public List<User> fidnAllByEmailNot(String email){
        return userRepository.findByEmailNot(email);
    }

    @Override
    public User update(User user, AuthDTO authDTO){
        Attributes attributes = null;
        try {
            attributes = authService.authenticate(user.getUsername(), authDTO.getPassword());
        } catch (NamingException e) {
            e.printStackTrace();
        }
        User newUser = null;
        try {
            newUser = authService.getUserInfo(attributes, user.getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newUser.setImagePath(user.getImagePath());
        newUser.setId(user.getId());
        return userRepository.save(newUser);
    }
}
