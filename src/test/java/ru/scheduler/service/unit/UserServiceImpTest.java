package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.repository.UserRepository;
import ru.scheduler.users.service.AuthService;
import ru.scheduler.users.service.UserServiceImp;

import java.util.ArrayList;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImpTest {
    @Mock
    UserRepository userRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    UserServiceImp userService;

    User user;
    AuthDTO authDTO;
    Attributes attributes;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setId(1L);
        user.setUsername("an123");
        user.setEmail("linux95@bk.ru");

        authDTO = new AuthDTO(user.getUsername(), "123");

        attributes = new BasicAttributes();
    }

    @Test
    public void getUserById() throws Exception {
        when(userRepository.findOne(user.getId())).thenReturn(user);
        userService.getUserById(user.getId());
        verify(userRepository).findOne(user.getId());
    }

    @Test
    public void addUser() throws Exception {
        when(userRepository.save(user)).thenReturn(user);
        userService.addUser(user);
        verify(userRepository).save(user);
    }

    @Test
    public void findUserByEmail() throws Exception {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        userService.findUserByEmail(user.getEmail());
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    public void findUserByUsername() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        userService.findUserByUsername(user.getUsername());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    public void save() throws Exception {
        when(userRepository.save(user)).thenReturn(user);
        userService.addUser(user);
        verify(userRepository).save(user);
    }

    @Test
    public void getAll() throws Exception {
        when(userRepository.findAll()).thenReturn(new ArrayList<User>());
        userService.getAll();
        verify(userRepository).findAll();
    }

    @Test
    public void fidnAllByEmailNot() throws Exception {
        when(userRepository.findByEmailNot(user.getEmail())).thenReturn(new ArrayList<User>());
        userService.fidnAllByEmailNot(user.getEmail());
        verify(userRepository).findByEmailNot(user.getEmail());
    }

    @Test
    public void update() throws Exception {
        when(authService.authenticate(authDTO.getUsername(), authDTO.getPassword())).thenReturn(attributes);
        when(authService.getUserInfo(attributes, user.getUsername())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        userService.update(user, authDTO);
        verify(authService).authenticate(authDTO.getUsername(), authDTO.getPassword());
        verify(authService).getUserInfo(attributes, user.getUsername());
        verify(userRepository).save(user);
    }

}