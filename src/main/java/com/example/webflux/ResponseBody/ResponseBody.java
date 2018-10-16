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

}
