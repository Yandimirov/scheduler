package ru.scheduler.messages.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.scheduler.config.View;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity(name = "CHATS")
@EqualsAndHashCode
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue
    @Column(name = "CHAT_ID", nullable =  false)
    @JsonView(View.MESSAGE.class)
    private long id;

    @Column(name = "NAME")
    @JsonView(View.MESSAGE.class)
    private String name;

    @Column(name = "PICTURE")
    @JsonView(View.MESSAGE.class)
    private String picture;
}
