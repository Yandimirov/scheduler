package ru.scheduler.users.restcontroller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;
import ru.scheduler.config.View;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;

    @Autowired
    private UserService userService;


    @Autowired
    private JwtService jwtService;

    @JsonView(View.SUMMARY.class)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User user(@PathVariable long id) {
        User user = userService.getUserById(id);
        return user;
    }

    @JsonView(View.SUMMARY.class)
    @RequestMapping(value = "/user/", method = RequestMethod.PUT)
    public User userUpadet(RequestEntity<AuthDTO> request) {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = jwtService.getUser(tokens.get(0));
        if (user == null) {
            return null;
        }
        if(user.getUsername().equals("miya0217") || user.getUsername().equals("anan1116")){
            return null;
        }
        return userService.update(user, request.getBody());
    }

    @JsonView(View.SUMMARY.class)
    @RequestMapping(value = "/users/{id}/role", method = RequestMethod.GET)
    public UserRole getRole(RequestEntity<?> request, @PathVariable long id){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for(String s : tokens){
            user = jwtService.getUser(s);
        }
        if(user != null){
            return userService.getRole(id);
        } else {
            return UserRole.USER;
        }
    }

    @JsonView(View.SUMMARY.class)
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getAllUsers(RequestEntity<?> request) {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        return userService.fidnAllByEmailNot(user.getEmail());
    }
}
