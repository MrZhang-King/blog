package com.zb.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@NoArgsConstructor
public class RequestResult<T> {
    private boolean success;
    private T data;
    private String message;

}
