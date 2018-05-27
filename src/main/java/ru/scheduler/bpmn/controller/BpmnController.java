package ru.scheduler.bpmn.controller;

import com.google.common.collect.ImmutableMap;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.service.JwtService;

@RestController
@RequestMapping("/api")
public class BpmnController {

    private final RuntimeService runtimeService;

    private final JwtService jwtService;

    @Autowired
    public BpmnController(RuntimeService runtimeService, JwtService jwtService) {
        this.runtimeService = runtimeService;
        this.jwtService = jwtService;
    }

    @PostMapping("/bpmn/event")
    public Object createEvents(@RequestHeader("x-auth-token") String token, @RequestBody EventDTO eventDto) {
        User user = jwtService.getUser(token);
        eventDto.setCreatedBy(user);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("create-event", ImmutableMap.of("eventDto", eventDto));
        return ((ExecutionEntityImpl) processInstance).getVariables().get("events");
    }
}
