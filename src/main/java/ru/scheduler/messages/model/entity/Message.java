package ru.scheduler.messages.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.config.View;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@Entity(name="MESSAGES")
@EqualsAndHashCode
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    @Column(name = "MESSAGE_ID", nullable = false)
    @JsonView(View.MESSAGE.class)
    private long id;

    @Column(name = "TEXT")
    @NotNull
    @JsonView(View.MESSAGE.class)
    private String text;

    @Column(name = "TIME_STAMP")
    @NotNull
    @JsonView(View.MESSAGE.class)
    private Date timeStamp;

    @ManyToOne
    @JoinColumn(name = "SENDER_ID")
    @JsonView(View.MESSAGE.class)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "CHAT_ID")
    @JsonView(View.MESSAGE.class)
    private Chat chat;
}
