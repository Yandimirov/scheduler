package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.config.View;
import ru.scheduler.users.model.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@Data
@Entity(name="USER_EVENTS")
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent implements Serializable {
    @Id
    @GeneratedValue
    @Column(name="USER_EVENT_ID")
    @JsonView(View.EVENT.class)
    private long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "EVENT_ID", referencedColumnName = "EVENT_ID"),
            @JoinColumn(name = "VERSION", referencedColumnName = "VERSION")
    })
    @JsonView(View.EVENT.class)
    private Event event;

    @JoinColumn(name="USER_ID")
    @ManyToOne
    @JsonView(View.EVENT.class)
    private User user;

    @OneToMany(mappedBy = "event")
    private List<EventNotification> notifications;

    @Enumerated(EnumType.STRING)
    @JsonView(View.EVENT.class)
    private UserEventStatus status;
}
