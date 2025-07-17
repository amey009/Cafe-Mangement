package com.m13.cafe.controller;


import com.m13.cafe.model.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/bill")
public interface BillController {

    @PostMapping("/generateReport")
    ResponseEntity<String> generateReport(@RequestBody Map<String,Object> requestMap);
    //why pass object in Map<String,Object>- So we're expecting one list of JSON array and that type is object

    @GetMapping("/getBill")
    ResponseEntity<List<Bill>> getBill();

    @PostMapping("/getPdf")
    ResponseEntity<byte[]> getPdf(@RequestBody Map<String,Object> requestMap);

    @PostMapping("/delete/{id}")
    ResponseEntity<String> deleteBill(@PathVariable Long id);
}
