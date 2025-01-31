package vttp.batch5.paf.Workshop_27.service;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.batch5.paf.Workshop_27.repo.GameRepo;

@Service
public class GameService {

    @Autowired
    private GameRepo gameRepo;

    public Document getGameWithReviews(Integer gameId) {
        return gameRepo.getGameWithReviews(gameId);
    }

    public List<Document> getGamesByRating(Integer order) {
        return gameRepo.getGamesByRating(order);
    }
}
