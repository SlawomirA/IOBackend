package com.example.iobackend.Model.Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MyResponse {
    private final int code;
    private final String message;
}