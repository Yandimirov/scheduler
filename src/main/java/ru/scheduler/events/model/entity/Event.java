package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import ru.scheduler.config.View;
import ru.scheduler.events.model.CompositeId;
import ru.scheduler.events.model.CompositeIdentifiable;
import ru.scheduler.events.model.entity.Event.EventId;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "EVENTS")
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class Event implements CompositeIdentifiable<EventId> {

    @EmbeddedId
    @GenericGenerator(
            name = "event-id-generator",
            strategy = "ru.scheduler.events.hibernate.EventIdSequenceIdentifier",
            parameters = {
                    @Parameter(name = "sequence_name", value = "event_info_id_sequence"),
                    @Parameter(name = "initial_value", value = "1")
            }
    )
    @GeneratedValue(generator = "event-id-generator", strategy = GenerationType.SEQUENCE)
    @JsonUnwrapped
    @JsonView(View.EVENT.class)
    private EventId compositeId;

    @ManyToOne
    @JoinColumn(name = "INFO_ID")
    @JsonView(View.EVENT.class)
    @NotNull
    private EventInfo info;

    @Column(name = "START_DATE")
    @NotNull
    @JsonView(View.EVENT.class)
    private Date startDate;

    @Column(name = "END_DATE")
    @NotNull
    @JsonView(View.EVENT.class)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @JsonView(View.EVENT.class)
    private EventType type;

    @ApiModelProperty(readOnly = true)
    @JsonView(View.EVENT.class)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt = new Date();

    public long getId() {
        return compositeId.getId();
    }

    public int getVersion() {
        return compositeId.getVersion();
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventId implements CompositeId {

        @Column(name = "EVENT_ID")
        @JsonView(View.EVENT.class)
        private long id;

        @Column(name = "VERSION")
        @ApiModelProperty(readOnly = true)
        @JsonView(View.EVENT.class)
        private int version = 1;

        public EventId(long id) {
            this.id = id;
        }
    }
}

