package com.nicolasmesa.springboot.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private List<String> errors;
}
