package com.example.webapp.services;

import com.example.webapp.entities.Device;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Primary
public class AdditionService extends CalculationService {

    @Override
    @Scheduled(fixedRate = 100)
    public void calculate() {
        if (inMemDataService.getConnections().size() > 0) {
            long result = 0;
            for (Map.Entry<Integer, Device> entry : inMemDataService.getConnections().entrySet()) {
                result += entry.getValue().getLastValue();
            }
            setResult(result + getParam());
        } else {
            setResult(null);
        }
    }
}
