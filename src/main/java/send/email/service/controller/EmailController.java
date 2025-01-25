package send.email.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import send.email.service.endpoints.EmailControllerEndpoints;
import send.email.service.model.dto.EmailDto;
import send.email.service.model.entity.CustomResponseEntity;
import send.email.service.service.EmailService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailController implements EmailControllerEndpoints {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public CustomResponseEntity<?> sendEmail(EmailDto emailDto) {
        return emailService.sendEmail(emailDto);
    }

    @Override
    public CustomResponseEntity<Map<String, String>> sendEmail(
             String[] to,
             String subject,
             String message) {
        Map<String, String> response = new HashMap<>();
        try {
            emailService.sendEmail(to, subject, message);
            response.put("status", "success");
            response.put("message", "Email sent successfully to " + String.join(", ", to));
            return new CustomResponseEntity<>(response, "Success");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            return CustomResponseEntity.error("Error: " + e.getMessage());
        }
    }

    @Override
    public CustomResponseEntity<String> sendEmailWithHtml(
             String to,
             String subject,
             String htmlContent) {
        try {
            emailService.sendEmailWithHtml(to, subject, htmlContent);
            return new CustomResponseEntity<>("HTML email sent successfully to " + to);
        } catch (Exception e) {
            return CustomResponseEntity.error("Failed to send HTML email: " + e.getMessage());
        }
    }

    @PostMapping("/sendWithAttachment")
    public CustomResponseEntity<String> sendEmailWithAttachment(
             String to,
             String subject,
             String message,
            @RequestParam("file") MultipartFile file) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            CustomResponseEntity<?> emailResponse = emailService.sendEmailWithFile(to, subject, message, convFile);

            return new CustomResponseEntity(emailResponse, "Success");
        } catch (Exception e) {
            return CustomResponseEntity.error("Failed to send email with attachment: " + e.getMessage());
        }
    }
}
