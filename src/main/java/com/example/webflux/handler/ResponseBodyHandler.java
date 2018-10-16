package com.example.webflux.handler;

import com.example.webflux.ResponseBody.ResponseBody;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class ResponseBodyHandler {

    public static <T> ResponseBody handleResponseBody(HttpStatus status, String path, T entity) {
        return new ResponseBody(new Date(), path, status.value(), entity);
    }
}
