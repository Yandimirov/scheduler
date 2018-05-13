package ru.scheduler.users.restcontroller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.scheduler.config.View;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.service.AuthService;

import javax.naming.NamingException;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @JsonView(View.AUTH.class)
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> auth(@RequestBody AuthDTO auth) {
        User user = null;
        String error = "";
        HttpStatus status = null;
        try {
            user = authService.getUser(auth);
        } catch (NamingException e){
            String message = e.getMessage().toLowerCase();
            if(message.contains("security")){
                error = "Некорректные имя пользователя или пароль";
                status = HttpStatus.UNAUTHORIZED;
            } else {
                error = "Отсутствует подключение к серверу аутентификации";
                status = HttpStatus.REQUEST_TIMEOUT;
            }
        } finally {
            if(user != null){
                return ResponseEntity.ok(user);
            } else {
                return new ResponseEntity<>(error, status);
            }
        }
    }

    @JsonView(View.AUTH.class)
    @RequestMapping(value = "/signout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestBody User user){
        if(authService.logout(user)){
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

}
