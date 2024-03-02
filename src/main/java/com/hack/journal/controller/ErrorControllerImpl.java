package com.hack.journal.controller;

import com.hack.journal.model.response.ErrorResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Error controller",
        description = """
                All unhandled exceptions during filtering are mapped to here.
                """
)
@CrossOrigin
@RestController
public class ErrorControllerImpl implements ErrorController {
    @GetMapping ("/error")
    public ResponseEntity<ErrorResponse> handleError(final HttpServletRequest request,
                                                     final HttpServletResponse response) {

        String errorMessage;

        HttpStatusCode httpStatus = response.getStatus() == 0 ? HttpStatus.BAD_REQUEST : HttpStatusCode.valueOf(response.getStatus());

        if (httpStatus == HttpStatus.FORBIDDEN) {
            errorMessage = "Access denied";
        } else {
            errorMessage = "Something went wrong. Check HTTP status code";
        }

        return new ResponseEntity<>(new ErrorResponse("Error", errorMessage), httpStatus);
    }

}
