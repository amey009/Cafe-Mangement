package com.m13.cafe.controller;

import com.m13.cafe.DTO.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/product")
public interface ProductController {

    @PostMapping("/add")
    ResponseEntity<String> addProduct(@RequestBody Map<String,String> requestMap);

    @GetMapping("/get")
    ResponseEntity<List<ProductDTO>> getAllProducts();

    @PostMapping("/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String,String> requestMap);

    @PostMapping("/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Long id);

    @PostMapping("/updateStatus")
    ResponseEntity<String> updateStatus(@RequestBody Map<String,String> requestMap);

    @GetMapping("/getByCategory/{id}")
    ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Long id);



}
