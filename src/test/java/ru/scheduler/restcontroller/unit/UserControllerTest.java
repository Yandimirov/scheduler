package ru.scheduler.restcontroller.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.restcontroller.UserController;
import ru.scheduler.users.service.AuthService;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    UserService userService;

    @Mock
    AuthService authService;

    @Mock
    JwtService jwtService;

    @InjectMocks
    UserController userController;

    String TOKEN_HEADER;
    String token;
    User user;
    AuthDTO authDTO;
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    @Before
    public void setUp() throws Exception {
        List<String> tokens = new ArrayList<>();
        token = "123";
        tokens.add(token);
        TOKEN_HEADER = "x-auth-token";
        headers.put(TOKEN_HEADER, tokens);

        user = new User();
        user.setId(1L);
        user.setUsername("an123");
        user.setEmail("linux95@bk.ru");

        ReflectionTestUtils.setField(userController, "TOKEN_HEADER", TOKEN_HEADER);

        authDTO = new AuthDTO("miya0217", "123");
    }

    @Test
    public void user() throws Exception {
        when(userService.getUserById(user.getId())).thenReturn(user);
        userController.user(user.getId());
        verify(userService).getUserById(user.getId());
    }

    @Test
    public void userUpadet() throws Exception {
        RequestEntity<AuthDTO> request = new RequestEntity<AuthDTO>(authDTO, headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        when(userService.update(user, request.getBody())).thenReturn(user);
        userController.userUpadet(request);
        verify(jwtService).getUser(token);
        verify(userService).update(user, request.getBody());

    }

    @Test
    public void getRole() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>(null, headers, null, null);
        when(authService.checkUser(request.getHeaders(), user.getId())).thenReturn(true);
        when(jwtService.getUser(token)).thenReturn(user);
        userController.getRole(request, user.getId());
        verify(authService).checkUser(request.getHeaders(), user.getId());
        verify(jwtService).getUser(token);
    }

    @Test
    public void getAllUsers() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>(null, headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        when(userService.fidnAllByEmailNot(user.getEmail())).thenReturn(new ArrayList<User>());
        userController.getAllUsers(request);
        verify(jwtService).getUser(token);
        verify(userService).fidnAllByEmailNot(user.getEmail());
    }

}