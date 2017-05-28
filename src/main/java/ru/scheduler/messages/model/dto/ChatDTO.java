package ru.scheduler.messages.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Mikhail Yandimirov on 29.04.2017.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    private String name;
    private List<Long> users;
}
