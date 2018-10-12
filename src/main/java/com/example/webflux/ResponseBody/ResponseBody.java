package com.example.webflux.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ResponseBody<T> {

    Date timestamp;

    String path;

    Integer status;

    T data;

    public ResponseBody<T> setPath(String path) {
        this.path = path;
        return this;
    }

}
