package com.visnevschi.familyhub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.repository.PersonaRepository;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final PersonaRepository personaRepository;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String mailFrom, PersonaRepository personaRepository) {
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.personaRepository = personaRepository;
    }

    @Transactional(readOnly = true)
    public void sendEmailNotification(Long personaId, String message) {
        personaRepository.findById(personaId)
        .map(Persona::getUserAccount)
        .filter(account -> account != null && account.getEmail() != null)
        .ifPresent(account -> sendEmail(
            account.getEmail(),
            "New Notification",
            "You have a new notification: " + message
        ));
    }


    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to: {}", to);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailFrom);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        try {
            mailSender.send(msg);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

}
