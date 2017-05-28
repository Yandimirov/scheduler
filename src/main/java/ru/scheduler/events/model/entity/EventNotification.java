package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.config.View;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity(name="EVENT_NOTIFICATIONS")
@AllArgsConstructor
@NoArgsConstructor
public class EventNotification {
    @Id
    @GeneratedValue
    @Column(name="NOTIFICATION_ID")
    @JsonView(View.EVENT.class)
    private long id;

    @JoinColumn(name="USER_EVENT_ID")
    @NotNull
    @ManyToOne
    @JsonView(View.EVENT.class)
    @JsonIgnore
    private UserEvent event;

    @Column(name="DATE")
    @JsonView(View.EVENT.class)
    private Date when;
}