package ru.scheduler.bpmn.controller;

import com.google.common.collect.ImmutableMap;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scheduler.events.model.dto.EventDTO;

@RestController
@RequestMapping("/api")
public class BpmnController {

    private final RuntimeService runtimeService;

    @Autowired
    public BpmnController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/bpmn/event")
    public Object createEvents(@RequestBody EventDTO eventDto) {
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("create-event", ImmutableMap.of("eventDto", eventDto));
        return ((ExecutionEntityImpl) processInstance).getVariables().get("events");
    }
}
