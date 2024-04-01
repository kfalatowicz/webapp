package com.example.webapp.controllers;

import com.example.webapp.services.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Autowired
    CalculationService calculationService;


    @GetMapping("/result")
    public ResponseEntity<?> getCurrentResult() {
        if (calculationService.getResult() != null) {
            return new ResponseEntity<>(new Object() {
                public Long result = calculationService.getResult();
            }, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/setParam")
    public ResponseEntity<?> setCalculationParam(@RequestParam Integer param) {
        calculationService.setParam(param);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
