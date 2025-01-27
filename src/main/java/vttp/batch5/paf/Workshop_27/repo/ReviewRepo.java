package vttp.batch5.paf.Workshop_27.repo;

import java.util.ArrayList;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import vttp.batch5.paf.Workshop_27.model.Edit;
import vttp.batch5.paf.Workshop_27.model.Game;
import vttp.batch5.paf.Workshop_27.model.Review;

@Repository
public class ReviewRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * db.reviews.insertOne({
     * user:"Mark",
     * rating:8.4,
     * comment:"This is one of the better games of 2024",
     * ID:1,
     * posted:new Date(),
     * name: db.games.findOne({gid:1}).name
     * })
     */
    public Review insertReview(String user, Float rating, String comment, Integer gameId) {

        // Get the game name from Games collection using gid
        Query gameQuery = new Query(Criteria.where("gid").is(gameId));
        Game game = mongoTemplate.findOne(gameQuery, Game.class, "game");

        if (game == null) {
            throw new IllegalArgumentException("Invalid game ID: " + gameId);
        }
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }

        // Create the Review object
        Review review = new Review();
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setGameId(game.getGid());
        review.setPostDate(new Date());
        review.setGameName(game.getName()); // Set the name from the game document
        review.setEditList(new ArrayList<>()); // Initialize the edit list as empty

        // Insert the review into the reviews collection
        mongoTemplate.insert(review, "reviews");

        return review;
    }

    /*
     * db.reviews.updateOne(
     * { _id: ObjectId("67947e156a62424263c191b8") }, // Filter
     * {
     * $push: { // Push the current fields into the edited array
     * edited: {
     * comment: "Update comment", // Previous comment
     * rating: 9, // Previous rating
     * posted: ISODate("2025-01-25T06:02:15.273+0000") // Previous posted timestamp
     * }
     * },
     * $set: { // Update with new fields
     * comment: "Amazing gameplay!",
     * rating: 9.2,
     * posted: new Date()
     * }
     * }
     * );
     * 
     * Task b
     */
    public UpdateResult updateReview(ObjectId reviewId, String comment, Float rating) {
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }

        // Fetch the existing review
        Query query = Query.query(Criteria.where("_id").is(reviewId));
        Review review = mongoTemplate.findOne(query, Review.class, "reviews");

        if (review == null) {
            throw new IllegalArgumentException("Review with ID " + reviewId + " does not exist.");
        }

        // Create a new Edit object for the current state
        Edit edit = new Edit();
        edit.setComment(review.getComment());
        edit.setPostDate(review.getPostDate());
        edit.setRating(review.getRating());

        // Add the new Edit object to the editList in MongoDB using $push
        Update updateOps = new Update()
                .push("editList", edit) // Add the previous state to the editList
                .set("comment", comment) // Update the comment
                .set("rating", rating) // Update the rating
                .set("posted", new Date()); // Update the posted date

        // Perform the update
        return mongoTemplate.upsert(query, updateOps, "reviews");
    }

    /*
     * db.reviews.findOne({
     * _id:ObjectId("67947e156a62424263c191b8")
     * //edited: true/false (check if edited is empty or not)
     * })
     * 
     * Task c
     */
    public JsonObject getReview(ObjectId reviewId) {
        Query query = Query.query(Criteria.where("_id").is(reviewId));
        Review result = mongoTemplate.findOne(query, Review.class, "reviews");

        boolean isEdited = result.getEditList() != null && !(result.getEditList().isEmpty());

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("user", result.getUser())
                .add("rating", result.getRating())
                .add("comment", result.getComment())
                .add("ID", result.getGameId())
                .add("posted", result.getPostDate().toString())
                .add("gameName", result.getGameName())
                .add("edited", isEdited)
                .add("timestamp", new Date().toString())
                .build();

        return jsonObject;
    }

    /*
     * db.reviews.findOne({
     * _id:ObjectId("67947e156a62424263c191b8")
     * })
     * 
     * Task d
     */
    public JsonObject getReviewWithEditHistory(ObjectId reviewId) {
        // Query to fetch the review
        Query query = Query.query(Criteria.where("_id").is(reviewId));
        Review result = mongoTemplate.findOne(query, Review.class, "reviews");

        // check if review with the queried ID exists in database
        if (result == null) {
            throw new IllegalArgumentException("Review with the given ID does not exist.");
        }

        // Build the JsonArray for editList
        JsonArrayBuilder editListArrayBuilder = Json.createArrayBuilder();
        if (result.getEditList() != null) {
            for (Edit edit : result.getEditList()) {
                JsonObject editJson = Json.createObjectBuilder()
                        .add("comment", edit.getComment())
                        .add("rating", edit.getRating())
                        .add("postDate", edit.getPostDate().toString())
                        .build();
                editListArrayBuilder.add(editJson);
            }
        }

        // Build the JsonObject for the review
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("user", result.getUser())
                .add("rating", result.getRating())
                .add("comment", result.getComment())
                .add("ID", result.getGameId())
                .add("posted", result.getPostDate().toString())
                .add("gameName", result.getGameName())
                .add("edited", editListArrayBuilder.build()) // Add the JsonArray for editList
                .add("timestamp", new Date().toString())
                .build();

        return jsonObject;
    }
}
