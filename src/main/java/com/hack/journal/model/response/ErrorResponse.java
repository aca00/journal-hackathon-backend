package com.hack.journal.model.response;

import lombok.*;
import org.springframework.web.bind.annotation.RequestMapping;

@ToString
@Getter
@Setter
@Builder
public class ErrorResponse  {
    private String error;
    private String message;
//    private long timestamp;

    public ErrorResponse(String message) {
        this(message, "Error");
    }

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
//        this.timestamp = System.currentTimeMillis();
    }

}
