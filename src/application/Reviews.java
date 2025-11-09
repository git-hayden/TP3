package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// manages the collection of reviews
public class Reviews {
	private List<Review> reviewList;
	
	// constructor
	public Reviews() {
		this.reviewList = new ArrayList<>();
	}
	
	// constructor with list of reviews
    public Reviews(List<Review> reviews) {
        this.reviewList = new ArrayList<>(reviews);
    }
    
    // add a new review
    public void addReview(Review review) {
        reviewList.add(review);
    }
    
    // get all reviews
    public List<Review> getAllReviews() {
        return new ArrayList<>(reviewList);
    }
    
    // get a specific review by ID
    public Review getReviewById(int reviewId) {
        return reviewList.stream()
            .filter(r -> r.getReviewId() == reviewId)
            .findFirst()
            .orElse(null);
    }
    
    // get all reviews for a specific question
    public Reviews getReviewsForQuestion(int questionId) {
        List<Review> filtered = reviewList.stream()
            .filter(r -> r.getQuestionId() == questionId)
            .collect(Collectors.toList());
        return new Reviews(filtered);
    }
    
    // get all reviews for a specific answer
    public Reviews getReviewsForAnswer(int answerId) {
        List<Review> filtered = reviewList.stream()
            .filter(r -> r.getAnswerId() == answerId)
            .collect(Collectors.toList());
        return new Reviews(filtered);
    }
    
    // update review
    public boolean updateReview(int reviewId, int newRating, String newContent) {
        Review review = getReviewById(reviewId);
        if (review != null) {
            review.setRating(newRating);
            review.setContent(newContent);
            return true;
        }
        return false;
    }
    
    // remove review
    public boolean deleteReview(int reviewId) {
        return reviewList.removeIf(r -> r.getReviewId() == reviewId);
    }
    
    // remove all reviews for a specific question
    public boolean deleteReviewsForQuestion(int questionId) {
        return reviewList.removeIf(r -> r.getQuestionId() == questionId);
    }
    
    // remove all reviews for a specific answer
    public boolean deleteReviewsForAnswer(int answerId) {
        return reviewList.removeIf(r -> r.getAnswerId() == answerId);
    }
    
    // search by content
    public Reviews searchByContent(String keyword) {
        List<Review> filtered = reviewList.stream()
            .filter(r -> r.getContent().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
        return new Reviews(filtered);
    }
    
    // author filter
    public Reviews filterByAuthor(String authorUserName) {
        List<Review> filtered = reviewList.stream()
            .filter(r -> authorUserName.equals(r.getAuthorUserName()))
            .collect(Collectors.toList());
        return new Reviews(filtered);
    }
    
    // rating filter
    public Reviews filterByRating(int rating) {
        List<Review> filtered = reviewList.stream()
            .filter(r -> r.getRating() == rating)
            .collect(Collectors.toList());
        return new Reviews(filtered);
    }
    
    // search reviews for a specific question
    public Reviews searchReviewsForQuestion(int questionId, String keyword) {
        List<Review> filtered = reviewList.stream()
            .filter(r -> r.getQuestionId() == questionId &&
                        r.getContent().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
        return new Reviews(filtered);
    }
    
    // search reviews for a specific question
    public Reviews searchReviewsForAnswer(int answerId, String keyword) {
        List<Review> filtered = reviewList.stream()
            .filter(r -> r.getAnswerId() == answerId &&
                        r.getContent().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
        return new Reviews(filtered);
    }
    
    // get count of reviews
    public int size() {
        return reviewList.size();
    }
    
    // check if empty
    public boolean isEmpty() {
        return reviewList.isEmpty();
    }
    
    // clear all reviews
    public void clear() {
        reviewList.clear();
    }
    
    // get count of reviews for question
    public int getReviewCountForQuestion(int questionId) {
        return (int) reviewList.stream()
            .filter(r -> r.getQuestionId() == questionId)
            .count();
    }
    
    // get count of reviews for answer
    public int getReviewCountForAnswer(int answerId) {
        return (int) reviewList.stream()
            .filter(r -> r.getAnswerId() == answerId)
            .count();
    }
}
