package ru.scheduler.restcontroller.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.restcontroller.AuthController;
import ru.scheduler.users.service.AuthService;

import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    AuthController controller;

    @Test
    public void auth() throws Exception {
        // prepare
        AuthDTO authDTO = new AuthDTO("miya0217", "123");
        Date date = Calendar.getInstance().getTime();

        User user = new User();

        when(authService.getUser(authDTO)).thenReturn(user);

        // testing
        ResponseEntity<?> response = controller.auth(authDTO);

        // validate

        verify(authService).getUser(authDTO);
    }

}