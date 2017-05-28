package ru.scheduler.users.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.scheduler.config.View;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "USERS")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonView(View.BASE.class)
    private Long id;

    @JsonView(View.MESSAGE.class)
    private String firstName;

    @JsonView(View.MESSAGE.class)
    private String lastName;

    @JsonView(View.SUMMARY.class)
    private Date birthday;

    @JsonView(View.MESSAGE.class)
    private String email;

    @JsonView(View.SUMMARY.class)
    private String username;

    @JsonView(View.SUMMARY.class)
    private String city;

    @JsonView(View.AUTH.class)
    private String token;

    @JsonView(View.BASE.class)
    private String imagePath;

    @JsonView(View.BASE.class)
    @Getter
    @Setter
    @Column(name="ROLE")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "FIRSTNAME")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    @Column(name = "LASTNAME")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "IMAGE_PATH")
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDAY")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    @Column(name = "CITY")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Column(name = "EMAIL", unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "TOKEN")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "USERNAME")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
