package vttp.batch5.paf.Workshop_27.repo;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * db.game.aggregate([
     * {
     * $match: { gid : 1}
     * },
     * {
     * $lookup: {
     * from: "reviews",
     * localField: "gid",
     * foreignField: "gameId",
     * as: "reviews"
     * }
     * },
     * {
     * $project: {
     * "game_id": "$gid",
     * "name": 1,
     * "year": 1,
     * "ranking": 1,
     * "average": 1,
     * "users_rated": 1,
     * "url": 1,
     * "image": 1,
     * "reviews": {
     * $map: {
     * input: "$reviews",
     * as: "review",
     * in: { $concat: ["/review/", { "$toString": "$$review._id" }] }
     * }
     * },
     * "timestamp": { $dateToString: { format: "%Y-%m-%dT%H:%M:%SZ", date: new
     * Date() } }
     * }
     * }
     * ])
     */
    public Document getGameWithReviews(Integer gameId) {
        // Step 1: Match the game with the given gameId
        MatchOperation matchGame = Aggregation.match(Criteria.where("gid").is(gameId));

        // Step 2: Lookup reviews for the game
        LookupOperation lookupReviews = Aggregation.lookup("reviews", "gid", "gameId", "reviews");

        // Step 3: Project the wanted fields
        ProjectionOperation projectGameWithReview = Aggregation.project(
                "name", "year", "ranking", "users_rated", "url", "image", "reviews")
                .and("gid").as("game_id")
                .and(DateOperators.DateToString.dateOf("$$NOW").toString("%Y-%m-%dT%H:%M:%SZ"))
                .as("timestamp");

        // Step 4: Build the aggregation pipeline
        Aggregation pipeline = Aggregation.newAggregation(matchGame, lookupReviews, projectGameWithReview);

        // Step 5: Execute the aggregation
        AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, "games", Document.class);

        // Step 6: Return the result (null if game does not exist)
        return results.getUniqueMappedResult();
    }

    /*
     * db.reviews.aggregate([
     * {
     * $lookup: {
     * from: "game",
     * localField: "gameId",
     * foreignField: "gid",
     * as: "games"
     * }
     * },
     *
     * {
     * $unwind: "$games"
     * },
     *
     * {
     * $group: {
     * _id: "$games.gid",
     * name: { $first: "$games.name" },
     * highestRating: { $max: "$rating" },
     * lowestRating: { $min: "$rating" },
     * user: { $first: "$user" },
     * comment: { $first: "$comment" },
     * review_id: { $first: "$_id" }
     * }
     * },
     *
     * {
     * $sort: { highestRating: -1 }
     * }
     * ])
     */
    public List<Document> getGamesByRating(Integer order) {
        LookupOperation lookupReviews = Aggregation.lookup("games", "gameId", "gid", "games");
        AggregationOperation unwindGames = Aggregation.unwind("games");
        GroupOperation groupByRatings = Aggregation.group("$games.gid")
                .first("games.name").as("name")
                .max("rating").as("highestRating")
                .min("rating").as("lowestRating")
                .first("user").as("user")
                .first("comment").as("comment")
                .first("_id").as("review_id");

        SortOperation sortByRating = Aggregation
                .sort(Sort.by(order == 1 ? Direction.ASC : Direction.DESC, "highestRating"));

        Aggregation pipeline = Aggregation.newAggregation(lookupReviews, unwindGames, groupByRatings, sortByRating);

        AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, "reviews", Document.class);
        return results.getMappedResults();
    }
}
