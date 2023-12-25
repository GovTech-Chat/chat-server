package com.bee.chat.util;

import com.bee.chat.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
//@AllArgsConstructor
public class EmailUtil {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private FileUtil fileUtil;

    public void sendNewUserEmail(User user, String password){
        Context context = new Context();
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("requestDate", new Date());
        context.setVariable("username", user.getUsername());
        context.setVariable("password", password);

        String htmlContent = templateEngine.process("new-user_en", context);

        sendEmailWithHtmlTemplate(user.getUsername(),
                "[GovTech Chat] Signup Successful",
                htmlContent);
    }

    public void sendEmailWithHtmlTemplate(String to, String subject, String htmlContent) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("to: {}", to);
            log.error("Send email error: {}", e.getMessage());
        }
    }
}
