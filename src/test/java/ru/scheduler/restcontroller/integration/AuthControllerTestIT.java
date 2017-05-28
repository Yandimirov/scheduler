package ru.scheduler.restcontroller.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import ru.scheduler.RestTemplateConfigurer;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthControllerTestIT {

    RestTemplate restTemplate;

    @Before
    public void before() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        restTemplate = RestTemplateConfigurer.configure();

    }

    @Test
    public void auth() {

        // prepare
        AuthDTO authDTO = new AuthDTO("miya0217", "123");
        HttpEntity<?> entity = new HttpEntity<>(authDTO);

        // testing
        ResponseEntity<User> responseEntity = restTemplate.exchange("https://localhost:8443/auth", HttpMethod.POST, entity,
                new ParameterizedTypeReference<User>() {
                });

        User user = responseEntity.getBody();
        // validate
        assertThat(user.getId(), is(1L));
        assertThat(user.getRole(), is(UserRole.MODERATOR));
    }
}