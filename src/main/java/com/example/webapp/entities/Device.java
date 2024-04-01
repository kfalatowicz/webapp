package com.example.webapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.Socket;
import java.time.LocalDateTime;

public class Device {

    private Integer canId;

    @JsonIgnore
    private Socket socket;

    private LocalDateTime lastSeen;

    private Integer lastValue;


    public Device(Integer canId, Socket socket, LocalDateTime lastSeen, Integer lastValue) {
        this.canId = canId;
        this.socket = socket;
        this.lastSeen = lastSeen;
        this.lastValue = lastValue;
    }

    public Integer getCanId() {
        return canId;
    }

    public void setCanId(Integer canId) {
        this.canId = canId;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Integer getLastValue() {
        return lastValue;
    }

    public void setLastValue(Integer lastValue) {
        this.lastValue = lastValue;
    }
}
