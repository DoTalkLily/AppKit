package com.randian.win.model;

import java.util.List;

/**
 * Created by lily on 15-8-22.
 */
public class Comment {
    private int score;
    private int comment_num;
    private List<CommentItem> comments;

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
