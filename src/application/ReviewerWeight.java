package application;

import java.time.LocalDateTime;

/**
 * ReviewerWeight represents the weight/trust level a student assigns to a specific reviewer.
 * This allows students to prioritize reviews from reviewers they trust more.
 */
public class ReviewerWeight {
    private int reviewerWeightId;
    private String studentUserName;
    private String reviewerUserName;
    private double weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for creating new reviewer weights
    public ReviewerWeight(String studentUserName, String reviewerUserName, double weight) {
        this.studentUserName = studentUserName;
        this.reviewerUserName = reviewerUserName;
        this.weight = weight;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for loading from database
    public ReviewerWeight(int reviewerWeightId, String studentUserName, String reviewerUserName,
                         double weight, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewerWeightId = reviewerWeightId;
        this.studentUserName = studentUserName;
        this.reviewerUserName = reviewerUserName;
        this.weight = weight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getReviewerWeightId() {
        return reviewerWeightId;
    }

    public String getStudentUserName() {
        return studentUserName;
    }

    public String getReviewerUserName() {
        return reviewerUserName;
    }

    public double getWeight() {
        return weight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setReviewerWeightId(int reviewerWeightId) {
        this.reviewerWeightId = reviewerWeightId;
    }

    public void setStudentUserName(String studentUserName) {
        this.studentUserName = studentUserName;
    }

    public void setReviewerUserName(String reviewerUserName) {
        this.reviewerUserName = reviewerUserName;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("ReviewerWeight[student=%s, reviewer=%s, weight=%.1f]",
                           studentUserName, reviewerUserName, weight);
    }
}
