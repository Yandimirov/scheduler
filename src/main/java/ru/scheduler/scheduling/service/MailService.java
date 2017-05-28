package ru.scheduler.scheduling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.scheduler.scheduling.model.dto.Mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private JavaMailSender javaMailSender;

    public void asyncSend(Mail mail){
        new Thread(() -> {
            try {
                send(mail);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void send(Mail mail) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(mail.getSubject());
        helper.setTo(mail.getTo());
        helper.setText(mail.getText(), true);
        javaMailSender.send(message);
    }
}
