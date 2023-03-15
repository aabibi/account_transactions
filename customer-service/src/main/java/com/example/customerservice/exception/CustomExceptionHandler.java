package com.example.customerservice.exception;



import com.example.customerservice.entity.model.ErrorResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGenericException(final Exception ex) {
        logger.error(
               String.format(
                        "Uncaught exception. Stack trace: %s\n", ex + getFullStackTraceLog(ex)));

        //  ExceptionMonitor.captureException(ex);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(),null, httpStatus, false);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler({SQLException.class, DataAccessException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleInvalidSQLError(
            Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = buildErrorResponse(ex, "Database SQL Error.", httpStatus,false);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundError(
            Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = buildErrorResponse(ex, "No data was found for based on  requested input.",httpStatus, false);
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundError(
            Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = buildErrorResponse(ex, "Account not found  based on requested input.",httpStatus, false);
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }


    @ExceptionHandler(InssuficientBalanceUpdateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserError(
            Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = buildErrorResponse(ex, "Transactions failed, not enough money to complete this.\"",httpStatus, false);
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }


    @ExceptionHandler(InvalidNegativeBalanceUpdateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNegativeBalance(
            Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = buildErrorResponse(ex, "Purchase/Installment Purchase/Withdrawal must be of negative amount.\"",httpStatus, false);
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameters(
            Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = buildErrorResponse(ex, "Invalid parameters sent. Please send a valid text/code.",httpStatus, true);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }


    protected ErrorResponse buildErrorResponse(Exception ex, String customMessage, HttpStatus httpStatus, boolean userCustomMessage) {
        return buildErrorResponse(ex.getMessage(), customMessage, httpStatus, userCustomMessage);
    }

    protected ErrorResponse buildErrorResponse(String message, String customMessage, HttpStatus httpStatus, boolean userCustomMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        if ( userCustomMessage) {
            errorResponse.setError(customMessage);
        } else {
            errorResponse.setError(message);
        }
        errorResponse.setStatus(httpStatus.value());
        return errorResponse;
    }



    protected String getFullStackTraceLog(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .map(Objects::toString)
                .collect(Collectors.joining("\n"));
    }


}