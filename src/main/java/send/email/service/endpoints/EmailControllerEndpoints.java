package send.email.service.endpoints;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import send.email.service.model.dto.EmailDto;
import send.email.service.model.entity.CustomResponseEntity;

import java.util.Map;

@RequestMapping("/v1/emails")
public interface EmailControllerEndpoints {

    //http://localhost:9090/v1/emails/send
    @PostMapping("/send")
    CustomResponseEntity<?> sendEmail(
            @RequestBody EmailDto emailDto);

    //http://localhost:9090/v1/emails/send/mail
    @PostMapping("/send/mail")
    CustomResponseEntity<Map<String, String>> sendEmail(
            @RequestParam String[] to,
            @RequestParam String subject,
            @RequestParam String message);

    @PostMapping("/sendWithHtml")
    CustomResponseEntity<String> sendEmailWithHtml(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String htmlContent);

    //http://localhost:9090/v1/emails/sendWithAttachment
    @PostMapping("/sendWithAttachment")
    CustomResponseEntity<String> sendEmailWithAttachment(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message,
            @RequestParam("file") MultipartFile file);
}
