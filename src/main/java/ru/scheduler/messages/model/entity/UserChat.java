package ru.scheduler.messages.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.config.View;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "USER_CHATS")
public class UserChat {

    @Id
    @GeneratedValue
    @Column(name = "USER_CHAT_ID", nullable =  false)
    @JsonView(View.MESSAGE.class)
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CHAT_ID")
    @JsonView(View.MESSAGE.class)
    private Chat chat;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_ID")
    @JsonView(View.MESSAGE.class)
    private User user;
}
