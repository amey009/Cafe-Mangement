package com.m13.cafe.service;

import com.m13.cafe.DTO.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    public ResponseEntity<String> signUp(Map<String,String> requestMap);

    public ResponseEntity<String> login(Map<String,String> requestMap);

    public ResponseEntity<List<UserDTO>> getAllUser();

    public ResponseEntity<String> update(Map<String,String> requestmap);

    public ResponseEntity<String> checkToken();

    ResponseEntity<String> changePassword(Map<String,String> requestmap);

    ResponseEntity<String> forgetPassword(Map<String, String> requestMap);
}
