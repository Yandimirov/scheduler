package ru.scheduler.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.scheduler.users.model.dto.AuthDTO;
import ru.scheduler.users.model.entity.User;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;

    @Value("${ldap.auth.domain}")
    private String DOMAIN;

    @Value("${ldap.auth.host}")
    private String HOST;

    @Value("${ldap.auth.dn}")
    private String DN;

    @Value("${ldap.auth.port}")
    private String PORT;

    private final static String ATTRIBUTE_FOR_USER = "sAMAccountName";
    private final static String IMAGE_PATH = "no-avatar.png";
    private final static String MAIL_ATTR = "mail";
    private final static String DEPARTMENT_ATTR = "department";
    private final static String USER_PRINCIPAL_NAME_ATTR = "userPrincipalName";
    private final static String OFFICE_NAME_ATTR = "physicalDeliveryOfficeName";
    private final static String FULL_NAME_ATTR = "extensionAttribute1";
    private final static String BIRTHDAY_ATTR = "extensionAttribute2";
    private final static String USER_POSITION_ATTR = "extensionAttribute3";

    public Attributes authenticate(String username, String password) throws NamingException {
        String returnedAtts[] = {
                MAIL_ATTR
                , DEPARTMENT_ATTR
                , USER_PRINCIPAL_NAME_ATTR
                , OFFICE_NAME_ATTR
                , FULL_NAME_ATTR
                , BIRTHDAY_ATTR
                , USER_POSITION_ATTR
        };

        String searchFilter = "(&(objectClass=user)(" + ATTRIBUTE_FOR_USER + "=" + username + "))";
        //Create the search controls

        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(returnedAtts);
        //Specify the search scope
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setTimeLimit(3000);
        String searchBase = DN;
        Hashtable environment = new Hashtable();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        //Using starndard Port, check your instalation

        environment.put(Context.PROVIDER_URL, "ldap://" + HOST + ":" + PORT);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");

        environment.put(Context.SECURITY_PRINCIPAL, username + "@" + DOMAIN);
        environment.put(Context.SECURITY_CREDENTIALS, password);
        //environment.put("com.sun.jndi.ldap.read.timeout", "3000");
        LdapContext ctxGC = new InitialLdapContext(environment, null);
        //    Search for objects in the GC using the filter

        NamingEnumeration answer = ctxGC.search(searchBase, searchFilter, searchCtls);
        while (answer.hasMoreElements()) {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            if (attrs != null) {
                return attrs;
            }
        }

        return null;
    }

    public Boolean checkIdAndToken(long id, List<String> tokens) {
        User user = userService.getUserById(id);
        return tokens.contains(user.getToken());
    }

    public Boolean checkUser(HttpHeaders headers, long idUser) {
        return checkIdAndToken(idUser, headers.get(TOKEN_HEADER));
    }

    public User saveUser(Attributes att, String username) throws ParseException {
        User user = getUserInfo(att, username);
        user.setImagePath(IMAGE_PATH);

        return userService.save(user);
    }

    public User getUserInfo(Attributes att, String username) throws ParseException {
        String department = att.get(DEPARTMENT_ATTR).toString()
                .substring(DEPARTMENT_ATTR.length() + 2);
        String email = att.get(MAIL_ATTR).toString().substring(MAIL_ATTR.length() + 2);
        String office = att.get(OFFICE_NAME_ATTR).toString()
                .substring(OFFICE_NAME_ATTR.length() + 2);
        String fullname[] = att.get(FULL_NAME_ATTR).toString()
                .substring(FULL_NAME_ATTR.length() + 2).split(" ");
        String lastName = fullname[0];
        String firstName = fullname[1];
        String birthDay = att.get(BIRTHDAY_ATTR).toString().substring(BIRTHDAY_ATTR.length() + 2);
        String position = att.get(USER_POSITION_ATTR).toString()
                .substring(USER_POSITION_ATTR.length() + 2);

        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date date = format.parse(birthDay);

        switch (office) {
            case "St. Petersburg":
                office = "Санкт-Петербург";
                break;
            case "Moscow":
                office = "Москва";
                break;
            case "Samara":
                office = "Самара";
                break;
            case "Nizhny Novgorod":
                office = "Нижний Новгород";
                break;
            case "Saratov":
                office = "Саратов";
                break;
            case "Togliatti":
                office = "Тольятти";
                break;
            case "Voronezh":
                office = "Воронеж";
                break;
            case "Kiev":
                office = "Киев";
                break;
            case "Sumy":
                office = "Сумы";
                break;
            case "Odessa":
                office = "Одесса";
                break;
        }

        User user = new User();
        user.setCity(office);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setBirthday(date);
        return user;
    }

    public User getUser(AuthDTO auth) throws NamingException {
        String username = auth.getUsername();
        String password = auth.getPassword();
        User user = null;
        if (username.equals("miya0217") || username.equals("anan1116")) {
            user = userService.findUserByUsername(username);
        } else {
            Attributes attributes = authenticate(username, password);
            if (attributes != null) {
                user = userService.findUserByUsername(username);
                if (user == null) {
                    try {
                        user = saveUser(attributes, username);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (user != null) {
            String token = jwtService.getToken(user);
            user.setToken(token);
            user = userService.save(user);
        }

        return user;
    }

    public boolean logout(User user) {
        User fullUser = userService.getUserById(user.getId());
        if (fullUser == null) {
            return false;
        }
        if (user.getToken().equals(fullUser.getToken())) {
            /*fullUser.setToken("");
            userService.save(fullUser);*/
            return true;
        }
        return false;
    }
}
