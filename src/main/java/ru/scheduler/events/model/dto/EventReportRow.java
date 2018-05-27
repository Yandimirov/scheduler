package ru.scheduler.events.model.dto;

import lombok.Data;
import lombok.NonNull;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.model.entity.Place;
import ru.scheduler.users.model.entity.User;

import java.util.Date;

/**
 * Created by Mikhail Yandimirov on 27.05.2018.
 */

@Data
public class EventReportRow {

    private long id;
    private long version;
    private String name;
    private String description;
    private String place;
    private String createdBy;
    private String email;
    private Date startDate;
    private Date endDate;
    private Date changedAt;

    public static EventReportRow fromEvent(@NonNull Event event) {
        EventReportRow reportRow = new EventReportRow();
        reportRow.setChangedAt(event.getCreatedAt());
        reportRow.setId(event.getId());
        reportRow.setVersion(event.getVersion());
        reportRow.setStartDate(event.getStartDate());
        reportRow.setEndDate(event.getEndDate());

        EventInfo info = event.getInfo();
        if (info != null) {
            User createdBy = info.getCreatedBy();
            if (createdBy != null) {
                reportRow.setCreatedBy(createdBy.getFirstName() + " " + createdBy.getLastName() + " " + createdBy.getEmail());
                reportRow.setEmail(createdBy.getEmail());
            }

            reportRow.setName(info.getName());
            reportRow.setDescription(info.getDescription());

            Place place = info.getPlace();
            if (place != null) {
                reportRow.setPlace(place.getName());
            }
        }

        return reportRow;
    }
}
