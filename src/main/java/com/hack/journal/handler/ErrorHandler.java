package com.hack.journal.handler;

import com.hack.journal.exception.*;
import com.hack.journal.model.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    ResponseEntity<?> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {

        if (e.getCause() instanceof DataAccessResourceFailureException) {
            return handleDataAccessResourceFailureException((DataAccessResourceFailureException) e.getCause());
        }

        return sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "InternalAuthenticationServiceException",
                e.getMessage());


    }

    @ExceptionHandler
    ResponseEntity<?> handleDataAccessResourceFailureException(DataAccessResourceFailureException e) {
        return sendErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "DataAccessResourceFailureException",
                e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleTemplateProcessingException(TemplateProcessingException e) {
        return sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "TemplateProcessingException",
                e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return sendErrorResponse(HttpStatus.FORBIDDEN,
                "AccessDeniedException",
                e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleNoBestSellerException(NoBestSellerException e) {
        return sendErrorResponse(HttpStatus.NOT_FOUND,
                "NoBestSellerException",
                e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return sendErrorResponse(HttpStatus.BAD_REQUEST,
                "MethodArgumentTypeMismatchException",
                e.getMessage());

    }

    @ExceptionHandler
    ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return sendErrorResponse(HttpStatus.BAD_REQUEST,
                "MethodArgumentNotValidException",
                e.getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleUserVerificationException(UserVerificationException e) {
        return sendErrorResponse(HttpStatus.UNAUTHORIZED,
                "UserVerificationException",
                e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleInsufficientPrivilegesException(InsufficientPrivilegesException exception) {
        return sendErrorResponse(HttpStatus.UNAUTHORIZED,
                "InsufficientPrivilegesException",
                exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleUnsupportedOperation(UnsupportedOperationException exception) {
        return sendErrorResponse(HttpStatus.FORBIDDEN,
                "UnsupportedOperationException", exception.getMessage());

    }

    @ExceptionHandler
    ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return sendErrorResponse(HttpStatus.BAD_REQUEST,
                "HttpMessageNotReadableException",
                "Request body missing (?)");

    }

    @ExceptionHandler
    ResponseEntity<?> handleNoSuchElementException(NoSuchElementException exception) {
        return sendErrorResponse(HttpStatus.NOT_FOUND,
                "Not Found", "Couldn't find requested item");
    }


    @ExceptionHandler
    ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return sendErrorResponse(HttpStatus.BAD_REQUEST,
                "HttpRequestMethodNotSupportedException",
                exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        return sendErrorResponse(
                HttpStatus.BAD_REQUEST,
                "MissingServletRequestParameterException",
                exception.getMessage()
        );
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleUserNotEnabledException(UserNotVerifiedException exception) {
        return sendErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "User not enabled. Contact admin"
        );
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        return sendErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                exception.getMessage()
        );
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException exception) {
        return sendErrorResponse(HttpStatus.UNAUTHORIZED,
                "Unauthorized", exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleDataIntegrityException(DataIntegrityViolationException exception) {
        return sendErrorResponse(HttpStatus.BAD_REQUEST,
                "DataIntegrityViolationException",
                "Violates schema definition");
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleUserAlreadyCreatedException(UserAlreadyCreatedException exception) {
        return sendErrorResponse(HttpStatus.UNAUTHORIZED,
                "UserAlreadyCreatedException",
                exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        return sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error", "Something bad happened." + exception.getClass() + exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> sendErrorResponse(
            HttpStatus httpStatus,
            String errorCode,
            String message
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(errorCode)
                .message(message)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return new ResponseEntity<>(errorResponse, headers, httpStatus);
    }

}
