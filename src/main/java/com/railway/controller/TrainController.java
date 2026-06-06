package com.railway.controller;

import com.railway.model.Train;
import com.railway.service.TrainService;
import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trains")
@CrossOrigin(origins = "*")
public class TrainController {

    private final TrainService trainService;

    // FIX: Use Constructor Injection. Spring will automatically provide the TrainService bean.
    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }
    
    // --- READ: Get All Trains (For List View) ---
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTrains() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Train> trains = trainService.getAllTrains();
            
            // CRITICAL: Calculate available seats before sending the list
            for (Train train : trains) {
                int seats = trainService.checkSeatAvailability(train.getTrainNumber());
                train.setAvailableSeats(seats);
            }
            
            response.put("success", true);
            response.put("data", trains);
            response.put("count", trains.size());
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", "Error retrieving all trains: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // --- READ: Search Trains ---
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTrains(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) String date) {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDate travelDate = date != null && !date.isEmpty() ? LocalDate.parse(date) : null;
            
            List<Train> trains = trainService.searchTrains(source, destination, travelDate);
            
            // CRITICAL: Calculate available seats before sending the list
            for (Train train : trains) {
                int seats = trainService.checkSeatAvailability(train.getTrainNumber());
                train.setAvailableSeats(seats);
            }
            
            response.put("success", true);
            response.put("data", trains);
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", "Error during search: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // --- READ: Get Single Train (for Edit Page) ---
    @GetMapping("/{trainNumber}")
    public ResponseEntity<Map<String, Object>> getTrainByNumber(@PathVariable String trainNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            Train train = trainService.getTrainByNumber(trainNumber); 
            if (train != null) {
                int seats = trainService.checkSeatAvailability(trainNumber);
                train.setAvailableSeats(seats);
                
                response.put("success", true);
                response.put("data", train);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Train not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // --- CREATE ---
    @PostMapping
    public ResponseEntity<Map<String, Object>> addTrain(@RequestBody Train train) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = trainService.addTrain(train);
            if (success) {
                response.put("success", true);
                response.put("message", "Train added successfully");
                response.put("data", train);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to add train");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // --- UPDATE ---
    @PutMapping("/{trainNumber}")
    public ResponseEntity<Map<String, Object>> updateTrain(
        @PathVariable String trainNumber,
        @RequestBody Train train) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            train.setTrainNumber(trainNumber); 
            boolean success = trainService.updateTrain(train);
            if (success) {
                response.put("success", true);
                response.put("message", "Train updated successfully");
                response.put("data", train);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to update train. Train not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // --- DELETE ---
    @DeleteMapping("/{trainNumber}")
    public ResponseEntity<Map<String, Object>> deleteTrain(@PathVariable String trainNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = trainService.deleteTrain(trainNumber);
            if (success) {
                response.put("success", true);
                response.put("message", "Train deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to delete train");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}