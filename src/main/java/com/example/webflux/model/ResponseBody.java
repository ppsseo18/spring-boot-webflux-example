package com.example.webflux.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ResponseBody<T> {

    Date timestamp;

    String path;

    Integer status;

    T data;

    public static <T> ResponseBody ok(String path, T entity) {
        return new ResponseBody(new Date(), path, HttpStatus.OK.value(), entity);
    }

    public static <T> ResponseBody created(String path, T entity) {
        return new ResponseBody(new Date(), path, HttpStatus.CREATED.value(), entity);
    }

}
