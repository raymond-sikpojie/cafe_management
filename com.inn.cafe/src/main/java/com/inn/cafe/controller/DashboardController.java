package com.inn.cafe.controller;

import com.inn.cafe.services.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/getDetails")
    ResponseEntity<Map<String, Object>> dashboardDetails() {
        return new ResponseEntity<>(dashboardService.getCount(), HttpStatus.OK);
    }

}
