package com.m13.cafe.controller;

import com.m13.cafe.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping("/user")
public interface UserController {

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String,String> requestMap);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String,String> requestMap);

    @GetMapping("/get")
    public  ResponseEntity<List<UserDTO>> getAllUser();

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody(required = true) Map<String,String> requestMap);

    @GetMapping("/checktoken")
    public  ResponseEntity<String> checkToken();

    @PostMapping("/chnagepassword")
    public ResponseEntity<String>  changePassword(@RequestBody Map<String,String> requestMap);

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgetPassword(@RequestBody Map<String , String> requestMap);

}
