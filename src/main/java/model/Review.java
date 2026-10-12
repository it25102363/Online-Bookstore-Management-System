package model;

public class Review {
    private String reviewId;
    private String isbn;
    private String userId;
    private int rating;
    private String comment;
    private String date;

    public Review(String reviewId, String isbn, String userId,
                  int rating, String comment, String date) {
        this.reviewId = reviewId;
        this.isbn = isbn;
        this.userId = userId;
        setRating(rating);
        setComment(comment);
        this.date = date;
    }

    public String getReviewId() { return reviewId; }
    public String getIsbn() { return isbn; }
    public String getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be 1-5");
        }
        this.rating = rating;
    }
    public void setComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        this.comment = comment.replace("|", " ");
    }
}
