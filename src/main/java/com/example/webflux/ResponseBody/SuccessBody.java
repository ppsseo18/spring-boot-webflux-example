package com.example.webflux.ResponseBody;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessBody<T> extends ResponseBody<T> {
    T data;

    public SuccessBody(HttpStatus status, T data) {
        super(status);
        this.data = data;
    }
}
