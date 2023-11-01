package cn.bctools.auth.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author guojing
 */
@JsonSerialize(using = CustomExceptionSerializer.class)
public class CustomException extends OAuth2Exception {
    @Getter
    private String code;

    public CustomException(String msg) {
        super(msg);
    }

    public CustomException(String msg, String code) {
        super(msg);
        this.code = code;
    }

    public CustomException(String message, Exception e) {
        super(message);
    }
}
