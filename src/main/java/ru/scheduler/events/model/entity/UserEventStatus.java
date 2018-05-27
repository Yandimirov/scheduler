package ru.scheduler.events.model.entity;

import java.io.Serializable;

/**
 * Created by Mikhail Yandimirov on 20.05.2018.
 */
public enum UserEventStatus implements Serializable {
    ACCEPTED,
    WAITED,
    REJECTED
}
