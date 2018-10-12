package com.example.webflux.ResponseBody;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorBody<T> extends ResponseBody<T> {
    String error;

    public ErrorBody (HttpStatus status, String error) {
        super(status);
        this.error = error;
    }
}
