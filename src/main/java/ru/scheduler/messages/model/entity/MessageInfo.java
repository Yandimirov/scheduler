package ru.scheduler.messages.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.config.View;

import javax.persistence.*;

/**
 * Created by Mikhail Yandimirov on 16.04.2017.
 */

@Data
@Entity(name = "MESSAGE_INFO")
public class MessageInfo {
    @Id
    @GeneratedValue
    @Column(name = "MSG_INFO_ID")
    @JsonView(View.MESSAGE.class)
    private long id;

    @JsonView(View.MESSAGE.class)
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "MESSAGE_ID")
    private Message message;

    @JsonView(View.MESSAGE.class)
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "USER_ID")
    private User user;

    @JsonView(View.MESSAGE.class)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
