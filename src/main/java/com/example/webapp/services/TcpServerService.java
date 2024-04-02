package com.example.webapp.services;

import com.example.webapp.entities.Device;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TcpServerService {

    @Value("${tcp.server.port}")
    private int serverPort;

    @Autowired
    InMemDataService inMemDataService;

    @Autowired
    CalculationService calculationService;



    @PostConstruct
    public void init() {
        new Thread(this::startServer).start();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("TCP Server started on port " + serverPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection accepted.");
                // Handle connection in thread
                new Thread(() -> handleConnection(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket clientSocket) {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
            while (true) {
                byte[] frameBytes = new byte[13];
                in.read(frameBytes);

                int canId = readCanId(frameBytes);
                int value = readData(frameBytes);

                inMemDataService.getConnections().put(canId, new Device(canId, clientSocket, LocalDateTime.now(), value));

                System.out.println("Received frame - CAN ID: " + canId + ", Value: " + value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (Map.Entry<Integer, Device> entry : inMemDataService.getConnections().entrySet()) {
                if (entry.getValue().getSocket() == clientSocket) {
                    inMemDataService.getConnections().remove(entry.getKey());
                    break;
                }
            }
        }
    }

    public void sendMessageToDevice(Integer deviceId, String message, boolean log) {
        Socket socket = inMemDataService.getConnections().get(deviceId).getSocket();
        if (socket != null) {
            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                byte[] frame = createFrame(deviceId, message);
                out.write(frame);
                out.flush();
                if (log) System.out.println("Message sent to device " + deviceId + ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Device " + deviceId + " is not connected.");
        }
    }

    @Scheduled(fixedRate = 100)
    public void sendResetMessageToDevices() {
        inMemDataService.getConnections().forEach((deviceId, device) -> {
            sendMessageToDevice(deviceId, "RESET", false);
        });
    }

    public static int readCanId(byte[] frame) {
        byte[] canIdBytes = new byte[4];
        System.arraycopy(frame, 1, canIdBytes, 0, 4);
        return byteArrayToInt(canIdBytes);
    }

    public static int readData(byte[] frame) {
        byte[] valueBytes = new byte[5];
        System.arraycopy(frame, 5, valueBytes, 0, 5);
        return byteArrayToInt(valueBytes);
    }

    private static int byteArrayToInt(byte[] bytes) {
        int length = bytes.length;
        if (length == 4) {
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            return ((bytes[0] & 0xFF)  << 32) | ((bytes[1] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 8) | (bytes[4] & 0xFF);
        }
    }

    private byte[] createFrame(int canId, String dataString) {
        byte[] frame = new byte[13];
        frame[0] = (byte) 0x05; // Frame format: extended frame; frame type: data frame; data length: 5
        frame[1] = (byte) (canId >> 24);  // CAN ID
        frame[2] = (byte) (canId >> 16);  // CAN ID
        frame[3] = (byte) (canId >> 8);   // CAN ID
        frame[4] = (byte) canId;          // CAN ID

        // Encode the string data
        byte[] dataBytes = dataString.getBytes();

        // Copy the encoded string data into the frame (up to 8 bytes)
        System.arraycopy(dataBytes, 0, frame, 5, Math.min(dataBytes.length, 8));

        // Pad the data bytes with zeros if necessary to ensure it's 8 bytes long
        for (int i = dataBytes.length; i < 8; i++) {
            frame[5 + i] = 0x00;
        }

        frame[10] = 0x00;       // Padding
        frame[11] = 0x00;       // Padding
        frame[12] = 0x00;       // Padding
        return frame;
    }

}
