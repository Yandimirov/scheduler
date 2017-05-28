package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.service.UserService;

import java.util.Base64;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtServiceTest {
    @Mock
    UserService userService;

    @InjectMocks
    JwtService jwtService;

    String plainSecret;
    String encodedSecret;
    Long expireHours;
    User user;
    String email;
    String token;

    @Before
    public void before(){
        plainSecret = "123456";
        expireHours = 24L;
        encodedSecret = Base64
                .getEncoder()
                .encodeToString(this.plainSecret.getBytes());
        ReflectionTestUtils.setField(jwtService, "plainSecret", plainSecret);
        ReflectionTestUtils.setField(jwtService, "encodedSecret", encodedSecret);
        ReflectionTestUtils.setField(jwtService, "expireHours", expireHours);
        User user = new User();
        email = "linux95@bk.ru";
        user.setEmail(email);
        token = jwtService.getToken(user);
    }

    @Test
    public void getUser() throws Exception {
        when(userService.findUserByEmail(email)).thenReturn(user);
        jwtService.getUser(token);
        verify(userService).findUserByEmail(email);
    }

}