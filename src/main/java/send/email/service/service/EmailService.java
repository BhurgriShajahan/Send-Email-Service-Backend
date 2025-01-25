package send.email.service.service;

import jakarta.mail.MessagingException;
import send.email.service.model.dto.EmailDto;
import send.email.service.model.entity.CustomResponseEntity;

import java.io.File;

public interface EmailService {

    //Send email to single person
    CustomResponseEntity<EmailDto> sendEmail(EmailDto emailDto);

    // Send email to multiple persons
    CustomResponseEntity<?> sendEmail(String []to,String subject,String message);

    //Send email with html
    CustomResponseEntity<?> sendEmailWithHtml(String to , String subject,String htmlContent) throws MessagingException;

    //Send email with file
    CustomResponseEntity<?> sendEmailWithFile(String to , String subject , String message , File file);

}
