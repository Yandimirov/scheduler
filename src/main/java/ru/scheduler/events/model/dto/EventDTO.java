package ru.scheduler.events.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.scheduler.events.model.entity.EventType;

import java.util.Date;

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
}
