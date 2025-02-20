package com.inn.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMailMessage(String currentUserEmail, String emailSubject, String emailBody, List<String> carbonCopyRecipients) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("omogbare@icloud.com");
            message.setSubject(emailSubject);
            message.setTo(currentUserEmail);
            message.setText(emailBody);

            if (!CollectionUtils.isEmpty(carbonCopyRecipients)) {
                String[] ccArray = getCarbonCopyRecipientsArray(carbonCopyRecipients);
                message.setCc(ccArray);
            }
            mailSender.send(message);
            log.info("Email sent to recipeints");
        } catch (Exception e) {
            log.error("An error occurred while sending email. " +  e.getMessage());
            e.printStackTrace();
        }

    }

    public void forgotPasswordEmail(String emailRecipient, String emailSubject, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("omogbare@icloud.com");
        messageHelper.setTo(emailRecipient);
        messageHelper.setSubject(emailSubject);

        String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + emailRecipient + " <br><b>Password: </b> " + password + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
        message.setContent(htmlMsg, "text/html");
        mailSender.send(message);


    }

    private String[] getCarbonCopyRecipientsArray(List<String> carbonCopyRecipientList) {
        String[] carbonCopyArray = new String[carbonCopyRecipientList.size()];
        for (int i = 0; i < carbonCopyRecipientList.size(); i++) {
            carbonCopyArray[i] = carbonCopyRecipientList.get(i);
        }
        return carbonCopyArray;
    }
}
