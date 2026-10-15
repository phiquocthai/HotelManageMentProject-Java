package com.example.hotel_management_project.Exceptions;

import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(CustomException e) {
        ApiResponse response = new ApiResponse();
        response.setCode(e.getErrorCode().getCode());
        response.setMessage(e.getErrorCode().getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        HashMap<String, String> errors = new HashMap<>();
        //e.getBindingResult().getFieldErrors():Lấy ds lỗi
        //.getField:Lấy tên trường bị lỗi
        //.getDefaultMessage():Lấy thông báo lỗi mặc định
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        String errorMessage=errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        ApiResponse response = new ApiResponse();
        response.setCode(400); // Bad Request
        response.setMessage(errorMessage);
        return ResponseEntity.badRequest().body(response);
    }
}
