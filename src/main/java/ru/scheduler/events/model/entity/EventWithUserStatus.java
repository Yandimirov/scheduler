package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Data;
import ru.scheduler.config.View;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Mikhail Yandimirov on 17.06.2018.
 */

@Data
@Builder
public class EventWithUserStatus {
    @JsonUnwrapped
    @JsonView(View.EVENT.class)
    private Event.EventId compositeId;

    @JsonView(View.EVENT.class)
    @NotNull
    private EventInfo info;

    @NotNull
    @JsonView(View.EVENT.class)
    private Date startDate;

    @NotNull
    @JsonView(View.EVENT.class)
    private Date endDate;

    @JsonView(View.EVENT.class)
    private Date createdAt;

    @JsonView(View.EVENT.class)
    private UserEventStatus status;
}
