package vttp.batch5.paf.Workshop_27.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;

import jakarta.json.JsonObject;
import vttp.batch5.paf.Workshop_27.model.Review;
import vttp.batch5.paf.Workshop_27.repo.ReviewRepo;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    public Review insertReview(String user, Float rating, String comment, Integer gameId) {
        return reviewRepo.insertReview(user, rating, comment, gameId);
    }

    public UpdateResult updateReview(ObjectId reviewId, String comment, Float rating) {
        return reviewRepo.updateReview(reviewId, comment, rating);
    }

    public JsonObject getReview(ObjectId reviewId) {
        return reviewRepo.getReview(reviewId);
    }

    public JsonObject getReviewWithEditHistory(ObjectId reviewId) {
        return reviewRepo.getReviewWithEditHistory(reviewId);
    }   
}
