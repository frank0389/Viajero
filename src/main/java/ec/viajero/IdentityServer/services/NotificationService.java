package ec.viajero.IdentityServer.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ec.viajero.IdentityServer.dto.UserDTO;
import ec.viajero.IdentityServer.model.EmailTemplate;
import ec.viajero.IdentityServer.util.Constants;
import ec.viajero.IdentityServer.util.EmailConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {
    @Value("${email.from}")
    private String from;

    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;

    public NotificationService(JavaMailSender javaMailSender,EmailTemplateService emailTemplateService ){
        this.javaMailSender=javaMailSender;
        this.emailTemplateService=emailTemplateService;
    }

    public boolean sendEmail(String to, String subject, String content) {
        log.info(
                "Send email to '{}' with subject '{}'",
                to,
                subject);

        try {

            MimeMessage msg = javaMailSender.createMimeMessage();
            // true = multipart message
            MimeMessageHelper helper = new MimeMessageHelper(msg, false);
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);

            helper.setText(content, true);
            javaMailSender.send(msg);
            return true;

        } catch (MailException | MessagingException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Async
    public void sendActivationEmail(UserDTO user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        this.emailTemplateService.template(EmailTemplate.Type.AccountActivation).map(template -> {
            String content = user.getLangKey().equals(Constants.DEFAULT_LANGUAGE)?template.getBody():template.getEnBody();
            if (content.contains(EmailConstants.EMAIL_USER_PARAM)) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                content = content.replace(EmailConstants.EMAIL_USER_PARAM, fullName);
            }

            if (content.contains(EmailConstants.EMAIL_CODE)) {
                content = content.replace(EmailConstants.EMAIL_CODE, user.getActivationKey());
            }

            String subject = user.getLangKey().equals(Constants.DEFAULT_LANGUAGE)?template.getSubject():template.getEnSubject();
            return sendEmail(user.getEmail(), subject, content);
        }).subscribe(t -> {

            if (t) {
                log.debug("Email  was send to User: '{}'", user.getEmail());
            } else {
                log.warn("Email could not be sent to user '{}'", user.getEmail());
            }
        });
    }

    @Async
    public void sendCreationEmail(UserDTO user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        this.emailTemplateService.template(EmailTemplate.Type.AccountConfirmation).map(template -> {
            String content = user.getLangKey().equals(Constants.DEFAULT_LANGUAGE)?template.getBody():template.getEnBody();
            if (content.contains(EmailConstants.EMAIL_USER_PARAM)) {
                String fullName = user.getFirstName() +" "+ user.getLastName();
                content = content.replace(EmailConstants.EMAIL_USER_PARAM, fullName);
            }

            String subject = user.getLangKey().equals(Constants.DEFAULT_LANGUAGE)?template.getSubject():template.getEnSubject();
            return sendEmail(user.getEmail(), subject, content);
        }).subscribe(t -> {
            if (t) {
                log.debug("Email  was send to User: '{}'", user.getEmail());
            } else {
                log.warn("Email could not be sent to user '{}'", user.getEmail());
            }
        });
    }

    @Async
    public void sendPasswordResetMail(UserDTO user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        this.emailTemplateService.template(EmailTemplate.Type.PasswordReset).map(template -> {

            String content = user.getLangKey().equals(Constants.DEFAULT_LANGUAGE)?template.getBody():template.getEnBody();
            if (content.contains(EmailConstants.EMAIL_USER_PARAM)) {
                content = content.replace(EmailConstants.EMAIL_USER_PARAM, user.getFirstName());
            }

            if (content.contains(EmailConstants.EMAIL_ACCOUNT)) {
                content = content.replace(EmailConstants.EMAIL_ACCOUNT, user.getUserName());
            }

            if (content.contains(EmailConstants.EMAIL_CODE)) {
                content = content.replace(EmailConstants.EMAIL_CODE, user.getResetKey());
            }
            
            String subject = user.getLangKey().equals(Constants.DEFAULT_LANGUAGE)?template.getSubject():template.getEnSubject();
            return sendEmail(user.getEmail(), subject, content);
        }).subscribe(t -> {
            if (t) {
                log.debug("Email  was send to User: '{}'", user.getEmail());
            } else {
                log.warn("Email could not be sent to user '{}'", user.getEmail());
            }
        });
    }

    @Async
    public void sendLoginMail(String firstName,String userName, String email, String langKey) {
        log.info("Sending login email to '{}'", userName);
        this.emailTemplateService.template(EmailTemplate.Type.AccountLogin).map(template -> {

            String content = langKey.equals(Constants.DEFAULT_LANGUAGE)?template.getBody():template.getEnBody();
            if (content.contains(EmailConstants.EMAIL_USER_PARAM)) {
                content = content.replace(EmailConstants.EMAIL_USER_PARAM, firstName);
            }

            if (content.contains(EmailConstants.EMAIL_ACCOUNT)) {
                content = content.replace(EmailConstants.EMAIL_ACCOUNT, userName);
            }

            String pattern = "dd/MM/YYYY hh:mm:ss";
            String currentDate =new SimpleDateFormat(pattern).format(new Date());

             if (content.contains(EmailConstants.EMAIL_DATE)) {
                content = content.replace(EmailConstants.EMAIL_DATE, currentDate);
            }


            String subject = langKey.equals(Constants.DEFAULT_LANGUAGE)?template.getSubject():template.getEnSubject();
            return sendEmail(email, subject, content);
        }).subscribe(t -> {
            if (t) {
                log.info("Email  was send to User: '{}'", email);
            } else {
                log.info("Email could not be sent to user '{}'", email);
            }
        });
    }
}