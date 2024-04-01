package com.example.webapp.services;

import com.example.webapp.entities.Device;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InMemDataService {

    private Map<Integer, Device> connections = new HashMap<>();


    public Map<Integer, Device> getConnections() {
        return connections;
    }

    public void setConnections(Map<Integer, Device> connections) {
        this.connections = connections;
    }
}
