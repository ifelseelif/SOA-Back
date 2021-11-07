package com.ifelseelif.servlets.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class HttpException extends Exception {
    private String message;
    private int statusCode;
}