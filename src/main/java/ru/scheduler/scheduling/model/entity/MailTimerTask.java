package ru.scheduler.scheduling.model.entity;

import lombok.Getter;
import lombok.Setter;
import ru.scheduler.scheduling.model.dto.Mail;
import ru.scheduler.events.model.entity.EventNotification;
import ru.scheduler.events.repository.EventNotificationRepository;
import ru.scheduler.scheduling.service.MailService;

import javax.mail.MessagingException;
import java.util.TimerTask;

/**
 * Created by Mikhail Yandimirov on 22.04.2017.
 */

public class MailTimerTask extends TimerTask {
    @Getter
    @Setter
    private long notificationId;

    @Setter
    private EventNotificationRepository eventNotificationRepository;

    @Setter
    private MailService mailService;

    @Override
    public void run() {
        EventNotification eventNotification = eventNotificationRepository.findOne(notificationId);
        if(eventNotification != null){
            StringBuilder mailTextBuilder = new StringBuilder();
            mailTextBuilder.append("Напоминание о событии <a href='https://localhost:8443/event/")
                    .append(eventNotification.getEvent().getEvent().getId())
                    .append("'>")
                    .append(eventNotification.getEvent().getEvent().getInfo().getName())
                    .append("</a>");

            Mail mail = Mail.builder()
                    .to(eventNotification.getEvent().getUser().getEmail())
                    .subject("Напомининание о событии!")
                    .text(mailTextBuilder.toString())
                    .build();
            try {
                mailService.send(mail);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
