package com.m13.cafe.controllerImpl;

import com.m13.cafe.controller.DashboardController;
import com.m13.cafe.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardControllerImpl implements DashboardController {

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getDetails() {
        try {
            return dashboardService.getDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
