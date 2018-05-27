package ru.scheduler.events.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.events.model.entity.EventType;
import ru.scheduler.users.model.entity.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO implements Serializable {
    private String name;
    private String description;
    private PlaceDTO place;
    private RepeatDTO repeats;
    private List<Long> userIds;
    private Date startDate;
    private Date endDate;
    private EventType type;
    private User createdBy;
}
