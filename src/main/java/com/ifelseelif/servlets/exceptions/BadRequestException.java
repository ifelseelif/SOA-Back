package com.ifelseelif.servlets.exceptions;


import javax.servlet.http.HttpServletResponse;

public class BadRequestException extends HttpException {

    public BadRequestException(String message) {
        super(message, HttpServletResponse.SC_BAD_REQUEST);
    }
}
