package vttp.batch5.paf.Workshop_27.model;

import java.util.Date;
import java.util.List;

public class Review {

    private String user;
    private Float rating;
    private String comment;
    private Integer gameId;
    private Date posted;
    private String gameName;
    private List<Edit> editList;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getPostDate() {
        return posted;
    }

    public void setPostDate(Date posted) {
        this.posted = posted;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<Edit> getEditList() {
        return editList;
    }

    public void setEditList(List<Edit> editList) {
        this.editList = editList;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
}
