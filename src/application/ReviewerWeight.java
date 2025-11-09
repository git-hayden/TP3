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

    /**
     * Creates a new ReviewerWeight for assigning a trust level to a reviewer.
     * Timestamps are automatically set to the current time.
     *
     * @param studentUserName the username of the student assigning the weight
     * @param reviewerUserName the username of the reviewer being weighted
     * @param weight the trust level value (typically between 0.1 and 5.0)
     */
    public ReviewerWeight(String studentUserName, String reviewerUserName, double weight) {
        this.studentUserName = studentUserName;
        this.reviewerUserName = reviewerUserName;
        this.weight = weight;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Creates a ReviewerWeight from existing database data.
     * Used when loading weights from the database.
     *
     * @param reviewerWeightId the unique identifier from the database
     * @param studentUserName the username of the student who assigned the weight
     * @param reviewerUserName the username of the reviewer
     * @param weight the trust level value
     * @param createdAt the timestamp when this weight was first created
     * @param updatedAt the timestamp when this weight was last modified
     */
    public ReviewerWeight(int reviewerWeightId, String studentUserName, String reviewerUserName,
                         double weight, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewerWeightId = reviewerWeightId;
        this.studentUserName = studentUserName;
        this.reviewerUserName = reviewerUserName;
        this.weight = weight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the unique database identifier for this reviewer weight.
     *
     * @return the reviewer weight ID
     */
    public int getReviewerWeightId() {
        return reviewerWeightId;
    }

    /**
     * Gets the username of the student who assigned this weight.
     *
     * @return the student's username
     */
    public String getStudentUserName() {
        return studentUserName;
    }

    /**
     * Gets the username of the reviewer being weighted.
     *
     * @return the reviewer's username
     */
    public String getReviewerUserName() {
        return reviewerUserName;
    }

    /**
     * Gets the trust level weight assigned to the reviewer.
     *
     * @return the weight value (typically between 0.1 and 5.0)
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Gets the timestamp when this weight was first created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the timestamp when this weight was last updated.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the database identifier for this reviewer weight.
     *
     * @param reviewerWeightId the unique identifier
     */
    public void setReviewerWeightId(int reviewerWeightId) {
        this.reviewerWeightId = reviewerWeightId;
    }

    /**
     * Sets the username of the student who assigned this weight.
     *
     * @param studentUserName the student's username
     */
    public void setStudentUserName(String studentUserName) {
        this.studentUserName = studentUserName;
    }

    /**
     * Sets the username of the reviewer being weighted.
     *
     * @param reviewerUserName the reviewer's username
     */
    public void setReviewerUserName(String reviewerUserName) {
        this.reviewerUserName = reviewerUserName;
    }

    /**
     * Sets the trust level weight for the reviewer.
     * Automatically updates the last modified timestamp.
     *
     * @param weight the new weight value (typically between 0.1 and 5.0)
     */
    public void setWeight(double weight) {
        this.weight = weight;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Sets the creation timestamp for this weight.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Sets the last update timestamp for this weight.
     *
     * @param updatedAt the update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of this reviewer weight.
     *
     * @return a formatted string containing student, reviewer, and weight information
     */
    @Override
    public String toString() {
        return String.format("ReviewerWeight[student=%s, reviewer=%s, weight=%.1f]",
                           studentUserName, reviewerUserName, weight);
    }
}
