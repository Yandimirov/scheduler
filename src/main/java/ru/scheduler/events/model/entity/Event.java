package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.config.View;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@Entity(name="EVENTS")
@NoArgsConstructor
public class Event {
    @Id
    @Column(name="EVENT_ID")
    @JsonView(View.EVENT.class)
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "INFO_ID")
    @JsonView(View.EVENT.class)
    @NotNull
    private EventInfo info;

    @Column(name="START_DATE")
    @NotNull
    @JsonView(View.EVENT.class)
    private Date startDate;

    @Column(name="END_DATE")
    @NotNull
    @JsonView(View.EVENT.class)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @JsonView(View.EVENT.class)
    private EventType type;
}

