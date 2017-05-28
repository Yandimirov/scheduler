package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.service.AuthService;
import ru.scheduler.users.service.UserService;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Mikhail Yandimirov on 13.05.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {
    @Mock
    UserService userService;

    @InjectMocks
    AuthService authService;

    Attributes attributes = new BasicAttributes();

    String username;

    String password;

    String email;

    @Before
    public void before(){
        attributes.put("sAMAccountName", "miia0217");
        attributes.put("physicalDeliveryOfficeName", "St. Petersburg");
        attributes.put("extensionAttribute1", "Яндимиров Михаил Аркадьевич");
        attributes.put("BIRTHDAY_ATTR", "12.11.1995");

        username = "miya0217";
        password = "123";
        email = "linux95@bk.ru";
    }



    @Test
    public void getUser() throws Exception {
        AuthDTO authDTO = new AuthDTO(username, password);
        when(userService.findUserByUsername(username)).thenReturn(new User());
        authService.getUser(authDTO);
        verify(userService).findUserByUsername(username);
    }

}