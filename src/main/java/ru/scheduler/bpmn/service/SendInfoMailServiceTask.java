package ru.scheduler.bpmn.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.scheduling.model.dto.Mail;
import ru.scheduler.scheduling.service.MailService;
import ru.scheduler.users.repository.UserRepository;

import java.util.Objects;

@Service
public class SendInfoMailServiceTask implements JavaDelegate {

    private final UserRepository userRepository;
    private final MailService mailService;

    @Value("${client.host.address}")
    private String clientAddress;

    @Autowired
    public SendInfoMailServiceTask(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        EventDTO eventDto = delegateExecution.getVariable("eventDto", EventDTO.class);

        eventDto.getUserIds().stream()
                .map(userRepository::findOne)
                .filter(Objects::nonNull)
                .map(user -> Mail.builder()
                        .subject("Приглашение на участие в событиях")
                        .to(user.getEmail())
                        .text("Привет, " + user.getFirstName() + "!\n" +
                                "у тебя есть новые приглашеия на участие в собтиях.\n" +
                                "Переходи по ссылке, чтобы узнать подробнее: <a>" + clientAddress + "</a>")
                        .build())
                .forEach(mailService::asyncSend);
    }
}
