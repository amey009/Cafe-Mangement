package com.m13.cafe.controller;

import com.m13.cafe.model.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/category")
public interface CategoryController {

    @PostMapping("/add")
    ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap);

    @GetMapping("/get")
    ResponseEntity<List<Category>>  getAllCategory(@RequestParam (required = false) String filterValue);

    @PostMapping("/update")
    ResponseEntity<String>  updateCategory(@RequestBody(required = true) Map<String, String> requestMap);



}
