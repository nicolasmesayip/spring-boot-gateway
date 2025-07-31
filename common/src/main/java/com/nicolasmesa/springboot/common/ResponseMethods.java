package com.nicolasmesa.springboot.common;

import com.nicolasmesa.springboot.common.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseMethods {

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return successResponse(data, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return successResponse(null, HttpStatus.NO_CONTENT);
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(List<String> error) {
        return errorResponse(error, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<ApiResponse<T>> unAuthorized(List<String> error) {
        return errorResponse(error, HttpStatus.UNAUTHORIZED);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(List<String> error) {
        return errorResponse(error, HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successResponse(T data, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> errorResponse(List<String> error, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setErrors(error);
        return ResponseEntity.status(status).body(response);
    }
}
