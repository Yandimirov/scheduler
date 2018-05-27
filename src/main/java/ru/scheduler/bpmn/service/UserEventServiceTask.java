package ru.scheduler.bpmn.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.UserEvent;
import ru.scheduler.events.model.entity.UserEventStatus;
import ru.scheduler.events.repository.UserEventRepository;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserEventServiceTask implements JavaDelegate {
    
    private final UserService userService;
    private final UserEventRepository userEventRepository;

    @Autowired
    public UserEventServiceTask(UserService userService, UserEventRepository userEventRepository) {
        this.userService = userService;
        this.userEventRepository = userEventRepository;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void execute(DelegateExecution delegateExecution) {
        EventDTO eventDto = delegateExecution.getVariable("eventDto", EventDTO.class);
        List<Event> events = delegateExecution.getVariable("events", List.class);

        List<UserEvent> userEvents = new ArrayList<>();
        List<Long> userIds = eventDto.getUserIds();
        events.forEach(event -> userIds.forEach(userId -> {
            User invitedUser = userService.getUserById(userId);
            UserEvent userEvent = new UserEvent();
            userEvent.setStatus(UserEventStatus.WAITED);
            userEvent.setEvent(event);
            userEvent.setUser(invitedUser);
            userEvents.add(userEventRepository.save(userEvent));
        }));

        delegateExecution.setVariable("userEvents", userEvents);
    }
}
