package ru.scheduler.events.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepeatDTO {
    private int value;
    private RepeatFreq freq;
    private Date until;
}
