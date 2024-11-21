package com.e_commerce.project.exceptions;


import com.e_commerce.project.payload.API_Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice   // get the Authority to customise the exceptions handling
public class MyGlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)  // Generic Exception Handler for all exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> response=new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(err->{
            String fieldName=((FieldError)err).getField();
            String message=err.getDefaultMessage();
            response.put(fieldName,message);
        });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<API_Response> myResourceNotFoundException(ResourceNotFoundException e){
        String message=e.getMessage();
        API_Response apiResponse=new API_Response(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(APIExceptions.class)
    public ResponseEntity<API_Response> myAPIException(APIExceptions e){
        String message=e.getMessage();
        API_Response apiResponse=new API_Response(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }



}
