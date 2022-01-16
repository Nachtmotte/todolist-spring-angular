package com.todolist.backend.controller.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseEntityUtil {

    public static ResponseEntity<Map<String, Object>> generateResponse(HttpStatus httpStatus, String objectName, Object object){
        Map<String, Object> bodyResponse = new HashMap<>();
        if(object != null){
            bodyResponse.put(objectName, object);
        }
        return new ResponseEntity<>(bodyResponse, httpStatus);
    }
}
