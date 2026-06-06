package com.railway.controller;

import com.railway.model.Feedback;
import com.railway.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    // FIX: Use Constructor Injection. Spring will automatically provide the FeedbackService bean.
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Handles the POST request for a passenger to submit new feedback.
     * Endpoint: POST /api/feedback
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody Feedback request) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = feedbackService.submitFeedback(request);
            if (success) {
                response.put("success", true);
                response.put("message", "Feedback submitted successfully.");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to submit feedback.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles the GET request to retrieve all feedback (Admin function).
     * Endpoint: GET /api/feedback
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFeedback() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbackList = feedbackService.getAllFeedback();
            response.put("success", true);
            response.put("data", feedbackList);
            response.put("count", feedbackList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to retrieve feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles the PUT request for an admin to update a specific feedback entry with a response.
     * Endpoint: PUT /api/feedback/{id}/respond
     */
    @PutMapping("/{id}/respond")
    public ResponseEntity<Map<String, Object>> updateAdminResponse(
            @PathVariable int id,
            @RequestBody AdminResponseRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = feedbackService.updateAdminResponse(id, request.getAdminResponse());
            if (success) {
                response.put("success", true);
                response.put("message", "Admin response recorded.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to find or update feedback ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Inner class for the Admin Response body (used in PUT method).
     */
    public static class AdminResponseRequest {
        private String adminResponse;

        public String getAdminResponse() {
            return adminResponse;
        }

        public void setAdminResponse(String adminResponse) {
            this.adminResponse = adminResponse;
        }
    }
}