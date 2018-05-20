package ru.scheduler.users.restcontroller;

import org.springframework.stereotype.Component;
import ru.scheduler.events.model.dto.EventDTO;

/**
 * Created by Mikhail Yandimirov on 20.05.2018.
 */

@Component
public class ExampleBean {

    public void doSomth() {
        System.out.println("doSomth called");
    }

    public void doSomth(EventDTO eventDTO) {
        System.out.println("doSomth called with eventDTO: " + eventDTO);
    }
}
