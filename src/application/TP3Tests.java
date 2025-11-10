/**
* TP3Tests - Automated Tests for Discussion Board
* <p>
* It tests CRUD operations for reviews
* 
* @author      Michael Mboudjeka
*/
package application;

import databasePart1.DiscussionBoardDAO;
import java.sql.SQLException;

/**
* The TP3Tests Class
*/
public class TP3Tests {

    private static int numPassed = 0;
    private static int numFailed = 0;
    private static DiscussionBoardDAO dao;
    private static final String USER = "tester";

    /**
    * Main method to execute the discussion board's automated tests
    * 
    * @param args  command line arguments (not used)
    * @since       1.0
    */
    public static void main(String[] args) {
        System.out.println("TP3Tests - Automated Tests\n");

        try {
            dao = new DiscussionBoardDAO();
            System.out.println("The automated testing is now starting!");
            System.out.println("________________________________________\n");

            // Run test cases
            runTest(1, "Add Review to Question", TP3Tests::testAddReviewToQuestion);
            runTest(2, "Add Review to Answer", TP3Tests::testAddReviewToAnswer);
            runTest(3, "Add Multiple Reviews to Question", TP3Tests::testAddMultipleReviewsToQuestion);
            runTest(4, "Add Multiple Reviews to Answer", TP3Tests::testAddMultipleReviewsToAnswer);
            runTest(5, "Update a Review", TP3Tests::testUpdateReview);
            runTest(6, "Delete a Review", TP3Tests::testDeleteReview);

            printTestResults();

        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
    * Runs a test case, handling exceptions
    *
    * @param id        the id for the test case
    * @param name      the name of the test case
    * @param testMethod    the method reference to the test implementation
    * @since               1.0
    */
    private static void runTest(int id, String name, testMethod testMethod) {
        System.out.println("Test #" + id + ": " + name);
        
        try {
            testMethod.run();
            System.out.println("Pass!");
            numPassed++;
        } catch (AssertionError e) {
            System.out.println("Fail: " + e.getMessage());
            numFailed++;
        } catch (Exception e) {
            System.out.println("Fail: " + e.getMessage());
            numFailed++;
        }
        System.out.println("________________________________________");
        System.out.println();
    }

    /**
    * Interface for test methods that throw SQLException
    * 
    * @since     1.0
    */
    @FunctionalInterface
    private interface testMethod {
        /**
        * Executes a test method that can throw SQLException
        * 
        * @throws SQLException if database operations fail during the test
        * @since               1.0
        */
        void run() throws SQLException;
    }
    
    /**
     * Test: The user can add a review to a question
     * <p>
     * Creates a question and adds a review to it, verifying that the 
     * review is properly stored.
     *
     * @throws SQLException     if database operations fail during the test
     * @throws AssertionError   if the test verification fails
     * @since                   1.0
     */
     public static void testAddReviewToQuestion() throws SQLException {
         System.out.println("Testing ability to add reviews to questions...");
         
         // Create a question
         Question question = new Question("Test Question", "This is the test question.", USER);
         dao.createQuestion(question);
         int questionId = question.getQuestionId();
         
         // Add a review
         Review review = new Review(5, questionId, -1, "Test review.", USER);
         dao.createReview(review);
         
         // Verify the review has been added
         Reviews reviews = dao.getReviewsForQuestion(questionId);
         assert reviews.size() == 1 : "Review not created!";
         Review created = reviews.getAllReviews().get(0);
         assert created.getQuestionId() == questionId : "Wrong question!";
         assert USER.equals(created.getAuthorUserName()) : "Wrong author!";
         
         // Reset
         dao.deleteReview(review.getReviewId());
         dao.deleteQuestion(questionId);
         System.out.println("Question review adding test complete.");
     }
     
     /**
      * Test: The user can add a review to an answer
      * <p>
      * Creates a question, gives it an answer, and adds a review to that answer,  
      * verifying that the review is properly stored.
      *
      * @throws SQLException     if database operations fail during the test
      * @throws AssertionError   if the test verification fails
      * @since                   1.0
      */
      public static void testAddReviewToAnswer() throws SQLException {
          System.out.println("Testing ability to add reviews to answers...");
          
          // Create a question
          Question question = new Question("Test Question", "This is the test question.", USER);
          dao.createQuestion(question);
          int questionId = question.getQuestionId();
          
          // Add an answer
          Answer answer = new Answer(questionId, "This is a test answer", USER);
          dao.createAnswer(answer);
          int answerId = answer.getAnswerId();
          
          // Add a review to the answer
          Review review = new Review(5, -1, answerId, "Test review.", USER);
          dao.createReview(review);
          
          // Verify the review has been added
          Reviews reviews = dao.getReviewsForAnswer(answerId);
          assert reviews.size() == 1 : "Review not created!";
          Review created = reviews.getAllReviews().get(0);
          assert created.getAnswerId() == answerId : "Wrong answer!";
          assert USER.equals(created.getAuthorUserName()) : "Wrong author!";
          
          // Reset
          dao.deleteReview(review.getReviewId());
          dao.deleteAnswer(answerId);
          dao.deleteQuestion(questionId);
          System.out.println("Answer review adding test complete.");
      }
     
    /**
    * Test: The user can add multiple reviews to the same question
    * <p>
    * Creates multiple reviews to a question and ensures that all the 
    * reviews are properly added.
    *
    * @throws SQLException     if database operations fail during the test
    * @throws AssertionError   if the test verification fails
    * @since                   1.0
    */
    public static void testAddMultipleReviewsToQuestion() throws SQLException {
        System.out.println("Testing ability to add multiple reviews to a question...");
        
        // Add a question
        Question question = new Question("Another test", "This may require several people.", USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        // Add several reviews
        String[] reviews = {
            "This looks good to me.",
            "I also think it looks fine.", 
            "No complaints from me!"
        };
        String[] authors = {"student1", "student2", "student3"};
        int[] ratings = {5, 5, 5};
        
        for (int i = 0; i < reviews.length; i++) {
            Review review = new Review(ratings[i], questionId, -1, reviews[i], authors[i]);
            dao.createReview(review);
        }
        
        // Verify each review
        Reviews theReviews = dao.getReviewsForQuestion(questionId);
        assert theReviews.size() == reviews.length : "Wrong amount of reviews!";
        
        // Reset
        for (Review review : theReviews.getAllReviews()) {
            dao.deleteReview(review.getReviewId());
        }
        dao.deleteQuestion(questionId);
        System.out.println("Multiple question review adding test complete.");
    }
    
    /**
     * Test: The user can add multiple reviews to the same answer
     * <p>
     * Creates multiple reviews to an answer and ensures that all the 
     * reviews are properly added.
     *
     * @throws SQLException     if database operations fail during the test
     * @throws AssertionError   if the test verification fails
     * @since                   1.0
     */
     public static void testAddMultipleReviewsToAnswer() throws SQLException {
         System.out.println("Testing ability to add multiple reviews to an answer...");
         
         // Add a question
         Question question = new Question("Another test", "This may require several people.", USER);
         dao.createQuestion(question);
         int questionId = question.getQuestionId();
         
         // Add an answer
         Answer answer = new Answer(questionId, "Let's see what happens!", USER);
         dao.createAnswer(answer);
         int answerId = answer.getAnswerId();
         
         // Add several reviews
         String[] reviews = {
             "This looks good to me.",
             "I also think it looks fine.", 
             "No complaints from me!"
         };
         String[] authors = {"student1", "student2", "student3"};
         int[] ratings = {5, 5, 5};
         
         for (int i = 0; i < reviews.length; i++) {
             Review review = new Review(ratings[i], -1, answerId, reviews[i], authors[i]);
             dao.createReview(review);
         }
         
         // Verify each review
         Reviews theReviews = dao.getReviewsForAnswer(answerId);
         assert theReviews.size() == reviews.length : "Wrong amount of reviews!";
         
         // Reset
         for (Review review : theReviews.getAllReviews()) {
             dao.deleteReview(review.getReviewId());
         }
         dao.deleteAnswer(answerId);
         dao.deleteQuestion(questionId);
         System.out.println("Multiple answer review adding test complete.");
     }
    
   /**
   * Test: The user can update their review
   * <p>
   * Creates a review, updates its content, and ensures
   * that the changes stick.
   *
   * @throws SQLException     if database operations fail during the test
   * @throws AssertionError   if the test verification fails
   * @since                   1.0
   */
   public static void testUpdateReview() throws SQLException {
       System.out.println("Testing ability to edit existing reviews...");
       
       // Create a question
       Question question = new Question("Test Question", "This is yet another test question.", USER);
       dao.createQuestion(question);
       int questionId = question.getQuestionId();
       
       // Add a review
       Review review = new Review(4, questionId, -1, "The original review.", USER);
       dao.createReview(review);
       int reviewId = review.getReviewId();
       
       // Update the review
       review.setRating(5);
       review.setContent("The updated review.");
       dao.updateReview(review);
       
       // Verify the update has stuck
       Reviews reviews = dao.getReviewsForQuestion(questionId);
       Review updated = reviews.getAllReviews().stream()
           .filter(a -> a.getReviewId() == reviewId)
           .findFirst().orElse(null);
           
       assert updated != null : "Review not found.";
       assert updated.getRating() == 5 : "Review not updated properly.";
       assert "The updated review".equals(updated.getContent()) : "Review not updated properly.";
       
       // Reset
       dao.deleteReview(reviewId);
       dao.deleteQuestion(questionId);
       System.out.println("Review updating test complete.");
   }
   
   /**
    * Test: The user can delete their review
    * <p>
    * Creates a review, deletes it, and ensures that 
    * the review is no longer in the database.
    *
    * @throws SQLException     if database operations fail during the test
    * @throws AssertionError   if the test verification fails
    * @since                   1.0
    */
    public static void testDeleteReview() throws SQLException {
        System.out.println("Testing ability to delete existing reviews...");
        
        // Create a question
        Question question = new Question("Test Question", "This is just a test question.", USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        // Add a review
        Review review = new Review(3, questionId, -1, "This is the review to be deleted.", USER);
        dao.createReview(review);
        int reviewId = review.getReviewId();
        
        // Delete the review
        dao.deleteReview(reviewId);
        
        // Verify the review is no longer in the database
        Reviews reviews = dao.getReviewsForQuestion(questionId);
        assert reviews.size() == 0 : "Review not deleted properly.";
        
        // Reset
        dao.deleteQuestion(questionId);
        System.out.println("Review deleting test complete.");
    }

    /**
    * Print out the results of the test
    * 
    * @since     1.0
    */
    private static void printTestResults() {
        System.out.println("Testing is now complete.");
        System.out.println("________________________________________\n");
        System.out.println("Total Tests: " + (numPassed + numFailed));
        System.out.println("Tests Passed: " + numPassed);
        System.out.println("Tests Failed: " + numFailed);
        
        if (numFailed == 0) {
            System.out.println("Congratulations, all tests passed!");
        } else {
            System.out.println("Oops, " + numFailed + " test(s) failed");
        }
    }
}