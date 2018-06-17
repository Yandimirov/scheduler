package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.scheduler.config.View;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity(name="PLACES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of={"id"})
public class Place implements Serializable {

    @Id
    @Column(name="PLACE_ID")
    @JsonView(View.EVENT.class)
    private String id;

    @JsonView(View.EVENT.class)
    @Column(name="LAT")
    @NotNull
    private double lat;

    @NotNull
    @Column(name="LON")
    @JsonView(View.EVENT.class)
    private double lon;

    @JsonView(View.EVENT.class)
    @Column(name="NAME")
    private String name;

    @JsonView(View.EVENT.class)
    @Column(name="DESCRIPTION")
    private String description;
}
