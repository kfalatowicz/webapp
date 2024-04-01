package com.example.webapp.services;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class CalculationService {

    @Autowired
    InMemDataService inMemDataService;

    private int param;

    private Long result;


    public abstract void calculate();


    public long getParam() {
        return param;
    }

    public void setParam(Integer param) {
        this.param = param;
    }

    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }
}
