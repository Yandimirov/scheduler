package ru.scheduler.messages.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private long recipientId;
    private String text;
    private Date timeStamp;
    private Long chatId;
}
