package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.scheduler.config.View;
import ru.scheduler.users.model.entity.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Mikhail Yandimirov on 16.04.2017.
 */

@Entity(name="EVENT_INFO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class EventInfo implements Serializable {
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

    @ManyToOne
    @JoinColumn(name = "CREATED_BY")
    @JsonView(View.EVENT.class)
    private User createdBy;
}
