package com.example.ggum.domain.post;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private int statusCode; // statusCode 추가

    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, data, "SUCCESS", 200); // statusCode 200 추가
    }

    public static <T> ApiResponse<T> onFailure(String message, int statusCode) {
        return new ApiResponse<>(false, null, message, statusCode); // statusCode 추가
    }
}