package com.example.webapp.controllers;

import com.example.webapp.entities.Device;
import com.example.webapp.services.InMemDataService;
import com.example.webapp.services.TcpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    TcpServerService tcpServerService;

    @Autowired
    InMemDataService inMemDataService;


    @GetMapping("/devices")
    public List<Device> getDevices() {
        return new ArrayList<>(inMemDataService.getConnections().values());
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getDevice(@PathVariable String id) {
        Device device = inMemDataService.getConnections().get(Integer.valueOf(id));
        if (device != null) {
            return new ResponseEntity<>(device, HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> setSendFreq(@PathVariable String id, @RequestParam Integer freq) {
        Device device = inMemDataService.getConnections().get(Integer.valueOf(id));
        if (device != null) {
            tcpServerService.sendMessageToDevice(device.getCanId(), "F " + freq);
            return new ResponseEntity<>(device, HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }




}
