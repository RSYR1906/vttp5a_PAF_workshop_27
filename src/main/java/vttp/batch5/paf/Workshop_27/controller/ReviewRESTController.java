package vttp.batch5.paf.Workshop_27.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import vttp.batch5.paf.Workshop_27.model.Review;
import vttp.batch5.paf.Workshop_27.service.ReviewService;

@RestController
public class ReviewRESTController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("review/{reviewId}")
    public ResponseEntity<String> getReview(@PathVariable ObjectId reviewId) {
        JsonObject review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(review.toString());
    }

    @GetMapping("review/{reviewId}/history")
    public ResponseEntity<?> getReviewWtihEditHistory(@PathVariable ObjectId reviewId) {
        JsonObject review = reviewService.getReviewWithEditHistory(reviewId);
        return ResponseEntity.ok(review.toString());
    }

    @PostMapping("/review")
    public ResponseEntity<?> insertReview(@RequestParam String user,
            @RequestParam Float rating,
            @RequestParam String comment,
            @RequestParam Integer id) {
        try {
            // Call service to insert the review
            Review review = reviewService.insertReview(user, rating, comment, id);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/review/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable ObjectId reviewId,
            @RequestParam Float rating,
            @RequestParam String comment) {
        try {
            // Call service to update the review
            reviewService.updateReview(reviewId, comment, rating);

            // Return a success response
            return ResponseEntity.ok().body("Update successful");
        } catch (IllegalArgumentException e) {
            // Handle validation errors or invalid reviewId
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}