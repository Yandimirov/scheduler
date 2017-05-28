package ru.scheduler.scheduling.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Mail {
    private String to;
    private String subject;
    private String text;
}
