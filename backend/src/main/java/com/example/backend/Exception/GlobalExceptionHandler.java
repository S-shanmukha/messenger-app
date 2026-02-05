package com.example.backend.Exception;

import com.example.backend.Dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("UserException",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("MessageException",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> handleChatException(ChatException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("ChatException",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e,
                                                                              WebRequest request) {
        ErrorResponse err = new ErrorResponse(e.getMessage(), request.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> noHandlerFoundExceptionHandler(NoHandlerFoundException e, WebRequest request) {
        ErrorResponse err = new ErrorResponse(e.getMessage(), request.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND); // You might want to use a different status code
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> otherExceptionHandler(Exception e, WebRequest request) {
//        ErrorResponse err = new ErrorResponse(e.getMessage(), request.getDescription(false), LocalDateTime.now());
//        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR); // You might want to use a different status
//        // code
//    }

}
