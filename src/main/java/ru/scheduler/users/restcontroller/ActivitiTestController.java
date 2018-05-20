package ru.scheduler.users.restcontroller;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.entity.EventType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikhail Yandimirov on 19.05.2018.
 */

@RestController
public class ActivitiTestController {

    @Autowired
    private RuntimeService runtimeService;

    @GetMapping("/start-process")
    public String startProcess() {

        Map<String, Object> map = new HashMap<>();
        EventDTO eventDTO = new EventDTO("test", "test", null, null, Collections.emptyList(),null, null, EventType.APPROVED, null);
        map.put("test", eventDTO);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", map);
        return "Process started. Number of currently running"
                + "process instances = "
                + runtimeService.createProcessInstanceQuery().count();
    }
}
