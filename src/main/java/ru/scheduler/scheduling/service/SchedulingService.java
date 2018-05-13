package ru.scheduler.scheduling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.scheduler.events.model.entity.EventNotification;
import ru.scheduler.events.repository.EventNotificationRepository;
import ru.scheduler.scheduling.model.dto.Mail;
import ru.scheduler.scheduling.model.entity.MailTimerTask;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.repository.UserRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

/**
 * Created by Mikhail Yandimirov on 22.04.2017.
 */
@Service
public class SchedulingService {

    @Autowired
    private EventNotificationRepository eventNotificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Value("${client.host.address}")
    private String CLIENT_ADDRESS;

    @Scheduled(cron="${cron.birthday.notifications}")
    public void congratulateWithBirthdays(){
        Calendar calendar = Calendar.getInstance();
        List<User> users = userRepository.findByBirthday(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1);
        Set<String> emails = new HashSet<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<p>Сегодня день рождения празднуют:</p><br/>");
        for(User user : users){
            emails.add(user.getEmail());
            stringBuilder.append("<a href='")
                    .append(CLIENT_ADDRESS)
                    .append("/users/")
                    .append(user.getId())
                    .append("'>")
                    .append(user.getFirstName())
                    .append(" ")
                    .append(user.getLastName())
                    .append("</a>");
        }
        List<User> usersForSendingMails = userRepository.findByEmailNotIn(emails);

        for(User user : usersForSendingMails) {
            Mail mail = Mail.builder()
                    .to(user.getEmail())
                    .text(stringBuilder.toString())
                    .subject("Сегодняшние именинники")
                    .build();
            mailService.asyncSend(mail);
        }
    }

    // TODO add for new notifications at today

    @Scheduled(cron="${cron.event.notifications}")
    public void scheduleAllNotifications(){
        Calendar calendar = Calendar.getInstance();
        Timer timer = new Timer();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();
        calendar.add(Calendar.HOUR, 24);
        Date nextDate = calendar.getTime();
        List<EventNotification> notifications = eventNotificationRepository.findByWhenGreaterThanEqualAndWhenLessThanEqual(now, nextDate);
        for(EventNotification eventNotification : notifications){
            MailTimerTask task = new MailTimerTask();
            task.setNotificationId(eventNotification.getId());
            task.setEventNotificationRepository(eventNotificationRepository);
            task.setMailService(mailService);
            timer.schedule(task, eventNotification.getWhen());
        }
    }
}
