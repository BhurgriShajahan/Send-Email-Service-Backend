package send.email.service.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import send.email.service.model.dto.EmailDto;
import send.email.service.model.entity.CustomResponseEntity;
import send.email.service.service.EmailService;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Retryable(
            value = {MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Async
    public CustomResponseEntity<EmailDto> sendEmail(EmailDto emailDto) {
        try {
            // Validate EmailDto fields
            if (emailDto == null || emailDto.getTo() == null || emailDto.getTo().isEmpty()) {
                logger.error("Recipient email address is null or empty.");
                return CustomResponseEntity.error("Recipient email address cannot be null or empty.");
            }
            if (emailDto.getSubject() == null || emailDto.getSubject().isEmpty()) {
                logger.error("Email subject is null or empty.");
                return CustomResponseEntity.error("Email subject cannot be null or empty.");
            }
            if (emailDto.getMessage() == null || emailDto.getMessage().isEmpty()) {
                logger.error("Email message is null or empty.");
                return CustomResponseEntity.error("Email message cannot be null or empty.");
            }

            // Prepare and send the email
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(emailDto.getTo());
            simpleMailMessage.setSubject(emailDto.getSubject());
            simpleMailMessage.setText(emailDto.getMessage());

            javaMailSender.send(simpleMailMessage);

            logger.info("Email has been sent to {}", emailDto.getTo());
            return new CustomResponseEntity<>("Email sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", emailDto != null ? emailDto.getTo() : "unknown", e.getMessage());
            return CustomResponseEntity.error("An error occurred while sending the email.");
        }
    }

    @Override
    @Async
    public CustomResponseEntity<?> sendEmail(String[] to, String subject, String message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            simpleMailMessage.setFrom("bhurgrishahjahan28@gmail.com");
            javaMailSender.send(simpleMailMessage);
            logger.info("Email has been sent to multiple recipients.");
            return new CustomResponseEntity<>("Email sent to multiple recipients successfully.");
        } catch (Exception e) {
            logger.error("Failed to send email to multiple recipients: {}", e.getMessage());
            return CustomResponseEntity.error("An error occurred while sending the email.");
        }
    }

    @Override
    @Async
    public CustomResponseEntity<?> sendEmailWithHtml(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("bhurgrishahjahan28@gmail.com");
            javaMailSender.send(mimeMessage);
            logger.info("HTML email has been sent to {}", to);
            return new CustomResponseEntity<>("HTML email sent successfully.");
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to {}: {}", to, e.getMessage());
            return CustomResponseEntity.error("An error occurred while sending the HTML email.");
        }
    }

    @Override
    public CustomResponseEntity<?> sendEmailWithFile(String to, String subject, String message, File file) {
        try {
            if (file == null || !file.exists() || !file.canRead()) {
                logger.error("The file does not exist or cannot be read: {}", file != null ? file.getAbsolutePath() : "null");
                return CustomResponseEntity.error("The file does not exist or cannot be read.");
            }

            String fileName = file.getName().toLowerCase();
            if (!isValidFileType(fileName)) {
                logger.error("File type not allowed: {}", fileName);
                return CustomResponseEntity.error("File type not allowed: " + fileName);
            }

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, false);
            helper.setFrom("bhurgrishahjahan28@gmail.com");

            FileSystemResource fileResource = new FileSystemResource(file);
            helper.addAttachment(file.getName(), fileResource);

            javaMailSender.send(mimeMessage);  // Actually sends the email here
            logger.info("Email with attachment has been sent to {}", to);
            return new CustomResponseEntity<>("Email with attachment sent successfully.");
        } catch (MessagingException e) {
            logger.error("Failed to send email with attachment to {}: {}", to, e.getMessage());
            return CustomResponseEntity.error("An error occurred while sending the email with attachment.");
        }
    }

    private boolean isValidFileType(String fileName) {
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".gif") || fileName.endsWith(".bmp") ||
                fileName.endsWith(".txt") || fileName.endsWith(".pdf") ||
                fileName.endsWith(".jar") || fileName.endsWith(".war") ||
                fileName.endsWith(".zip");
    }

}
