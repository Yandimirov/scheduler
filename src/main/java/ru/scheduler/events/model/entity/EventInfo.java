package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.config.View;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Mikhail Yandimirov on 16.04.2017.
 */

@Entity(name="EVENT_INFO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventInfo {
    @Id
    @GeneratedValue
    @Column(name = "INFO_ID")
    @JsonView(View.EVENT.class)
    private long id;

    @Column(name = "NAME")
    @JsonView(View.EVENT.class)
    @NotNull
    private String name;

    @Column(name = "DESCRIPTION")
    @JsonView(View.EVENT.class)
    private String description;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="PLACE_ID")
    @JsonView(View.EVENT.class)
    private Place place;
}
