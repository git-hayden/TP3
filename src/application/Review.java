package application;

import java.time.LocalDateTime;

public class Review {
	private int reviewId;
	private int questionId;
	private int answerId;
	private int rating;
	private String content;
	private String authorUserName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	// constructor getter and setter
	public Review(int rating, int questionId, int answerId, String content, String authorUserName) {
		this.rating = rating;
		this.questionId = questionId;
		this.answerId = answerId;
		this.content = content;
		this.authorUserName = authorUserName;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	
	// constructor with all fields for db retrieval
    public Review(int reviewId, int questionId, int answerId, int rating, String content, String authorUserName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewId = reviewId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.rating = rating;
        this.content = content;
        this.authorUserName = authorUserName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
	
	// getters and setters
	public int getReviewId() {
		return reviewId;
	}
	public int getQuestionId() {
		return questionId;
	}
	public int getAnswerId() {
		return answerId;
	}
	public int getRating() {
		return rating;
	}
	public String getContent() {
		return content;
	}
	public String getAuthorUserName() {
		return authorUserName;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}
	public void setRating(int rating) {
		this.rating = rating;
		this.updatedAt = LocalDateTime.now(); // update the updatedAt time
	}
	public void setContent(String content) {
		this.content = content;
		this.updatedAt = LocalDateTime.now(); // update the updatedAt time
	}
	public void setAuthorUserName(String authorUserName) {
		this.authorUserName = authorUserName;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	// Display the review
	@Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", questionId=" + questionId +
                ", answerId=" + answerId +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                ", authorUserName='" + authorUserName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
