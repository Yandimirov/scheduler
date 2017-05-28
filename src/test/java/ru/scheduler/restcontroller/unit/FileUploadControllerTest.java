package ru.scheduler.restcontroller.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.restcontroller.FileUploadController;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.service.StorageService;
import ru.scheduler.users.service.UserService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadControllerTest {

    @Mock
    JwtService jwtService;

    @Mock
    UserService userService;

    @Mock
    StorageService storageService;

    @InjectMocks
    FileUploadController controller;

    private final String IMAGE_PATH = "no-avatar.png";

    String token;
    User user;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setId(1L);
        user.setImagePath(IMAGE_PATH);

        token = "123";
    }

    @Test
    public void serveStdFile() throws Exception {
        when(storageService.loadAsResource(IMAGE_PATH)).thenReturn(new ClassPathResource("123"));
        controller.serveStdFile();
        verify(storageService).loadAsResource(IMAGE_PATH);
    }

    @Test
    public void serveFile() throws Exception {
        when(storageService.loadAsResource(IMAGE_PATH)).thenReturn(new ClassPathResource("123"));
        controller.serveStdFile();
        verify(storageService).loadAsResource(IMAGE_PATH);
    }
}