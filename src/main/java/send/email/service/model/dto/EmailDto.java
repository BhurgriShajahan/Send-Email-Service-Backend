package send.email.service.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {

    private String to;
    private String subject;
    private String message;
}
