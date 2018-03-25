package ru.scheduler.events.model.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.events.model.entity.EventType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String name;
    private String description;
    private PlaceDTO place;
    private RepeatDTO repeats;
    private Date startDate;
    private Date endDate;
    private EventType type;
    private Date createdAt = new Date();
}
