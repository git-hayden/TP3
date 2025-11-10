/**
* HW3Testbed - Automated Tests for Discussion Board
* <p>
* It tests CRUD operations for replies
* 
* @author      Michael Mboudjeka
*/
package application;

import databasePart1.DiscussionBoardDAO;
import java.sql.SQLException;

/**
* The HW3Testbed Class
*/
public class HW3Testbed {

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
        System.out.println("HW3Testbed - Automated Tests\n");

        try {
            dao = new DiscussionBoardDAO();
            System.out.println("The automated testing is now starting!");
            System.out.println("________________________________________\n");

            // Run test cases
            runTest(1, "Add Reply to Answer", HW3Testbed::testAddReplyToAnswer);
            runTest(2, "Add Multiple Replies to Answer", HW3Testbed::testAddMulitpleReplies);
            runTest(3, "Add Reply To Nonexistent Answer", HW3Testbed::testAddReplyWithoutAnswer);
            runTest(4, "Update a Reply", HW3Testbed::testUpdateReply);
            runTest(5, "Delete a Reply", HW3Testbed::testDeleteReply);

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
     * Test: The user can add a reply to an answer for further clarification
     * <p>
     * Creates a question, adds an answer to it, then adds a reply to that answer,
     * verifying that the reply is properly stored.
     *
     * @throws SQLException     if database operations fail during the test
     * @throws AssertionError   if the test verification fails
     * @since                   1.0
     */
     public static void testAddReplyToAnswer() throws SQLException {
         System.out.println("Testing ability to add replies...");
         
         // Create a question
         Question question = new Question("Test Question", "This is the test question.", USER);
         dao.createQuestion(question);
         int questionId = question.getQuestionId();
         
         // Add an answer
         Answer answer = new Answer(questionId, "Test answer.", USER);
         dao.createAnswer(answer);
         int answerId = answer.getAnswerId();
         
         // Add a reply to the answer
         Reply reply = new Reply(answerId, "Test reply.", USER);
         dao.createReply(reply);
         
         // Verify the reply has been added
         Replies replies = dao.getRepliesForAnswer(answerId);
         assert replies.size() == 1 : "Reply not created!";
         Reply created = replies.getAllReplies().get(0);
         assert created.getAnswerId() == answerId : "Wrong answer!";
         assert USER.equals(created.getAuthorUserName()) : "Wrong author!";
         
         // Reset
         dao.deleteReply(reply.getReplyId());
         dao.deleteAnswer(answer.getAnswerId());
         dao.deleteQuestion(questionId);
         System.out.println("Reply adding test complete.");
     }
     
    /**
    * Test: The user can add multiple replies to the same question
    * <p>
    * Creates multiple replies to one answer to a question and
    * ensures that all the replies are properly added.
    *
    * @throws SQLException     if database operations fail during the test
    * @throws AssertionError   if the test verification fails
    * @since                   1.0
    */
    public static void testAddMulitpleReplies() throws SQLException {
        System.out.println("Testing ability to add multiple replies...");
        
        // Add a question
        Question question = new Question("Another test", "This may require several people.", USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        // Add an answer
        Answer answer = new Answer(questionId, "Let's see what we can do!", USER);
        dao.createAnswer(answer);
        int answerId = answer.getAnswerId();
        
        // Add several replies
        String[] replies = {
            "This looks good to me.",
            "I also think it looks fine.", 
            "No complaints from me!"
        };
        String[] authors = {"student1", "student2", "student3"};
        
        for (int i = 0; i < replies.length; i++) {
            Reply reply = new Reply(answerId, replies[i], authors[i]);
            dao.createReply(reply);
        }
        
        // Verify each reply
        Replies theReplies = dao.getRepliesForAnswer(answerId);
        assert theReplies.size() == replies.length : "Wrong amount of replies!";
        
        // Reset
        for (Reply reply : theReplies.getAllReplies()) {
            dao.deleteReply(reply.getReplyId());
        }
        dao.deleteAnswer(answerId);
        dao.deleteQuestion(questionId);
        System.out.println("Multiple reply adding test complete.");
    }
    
   /**
   * Test: The user can't add a reply without an answer
   * <p>
   * Tries to create a reply to a nonexistent answer, verifying
   * that the system shows an error message.
   *
   * @throws SQLException     if database operations fail during the test
   * @throws AssertionError   if the test verification fails
   * @since                   1.0
   */
   public static void testAddReplyWithoutAnswer() throws SQLException {
       System.out.println("Testing inability to add answerless replies...");
       
       try {
           // Attempt to add a reply to a nonexistent answer
           Reply badReply = new Reply(-1, "This shouldn't work!", USER);
           dao.createReply(badReply);
           
           // If no error is thrown, the test has failed
           throw new AssertionError("Should have thrown an SQLException");
           
       } catch (SQLException e) {
           // This is what should happen
           System.out.println("Error Message: " + e.getMessage());
       }
   } 
    
   /**
   * Test: The user can update their reply
   * <p>
   * Creates a reply, updates its content, and ensures
   * that the changes stick.
   *
   * @throws SQLException     if database operations fail during the test
   * @throws AssertionError   if the test verification fails
   * @since                   1.0
   */
   public static void testUpdateReply() throws SQLException {
       System.out.println("Testing ability to edit existing replies...");
       
       // Create a question
       Question question = new Question("Test Question", "This is yet another test question.", USER);
       dao.createQuestion(question);
       int questionId = question.getQuestionId();
       
       // Add an answer
       Answer answer = new Answer(questionId, "And this is another test answer.", USER);
       dao.createAnswer(answer);
       int answerId = answer.getAnswerId();
       
       // Add a reply
       Reply reply = new Reply(answerId, "The original reply.", USER);
       dao.createReply(reply);
       int replyId = reply.getReplyId();
       
       // Update the reply
       reply.setContent("The updated reply.");
       dao.updateReply(reply);
       
       // Verify the update has stuck
       Replies replies = dao.getRepliesForAnswer(answerId);
       Reply updated = replies.getAllReplies().stream()
           .filter(a -> a.getReplyId() == replyId)
           .findFirst().orElse(null);
           
       assert updated != null : "Reply not found.";
       assert "The updated reply".equals(updated.getContent()) : "Reply not updated properly.";
       
       // Reset
       dao.deleteReply(replyId);
       dao.deleteAnswer(answerId);
       dao.deleteQuestion(questionId);
       System.out.println("Reply updating test complete.");
   }
   
   /**
    * Test: The user can delete their reply
    * <p>
    * Creates a reply, deletes it, and ensures that 
    * the reply is no longer in the database.
    *
    * @throws SQLException     if database operations fail during the test
    * @throws AssertionError   if the test verification fails
    * @since                   1.0
    */
    public static void testDeleteReply() throws SQLException {
        System.out.println("Testing ability to delete existing replies...");
        
        // Create a question
        Question question = new Question("Test Question", "This is just a test question.", USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        // Add an answer
        Answer answer = new Answer(questionId, "And this is just a test answer.", USER);
        dao.createAnswer(answer);
        int answerId = answer.getAnswerId();
        
        // Add a reply
        Reply reply = new Reply(answerId, "This is the reply to be deleted.", USER);
        dao.createReply(reply);
        int replyId = reply.getReplyId();
        
        // Delete the reply
        dao.deleteReply(replyId);
        
        // Verify the reply is no longer in the database
        Replies replies = dao.getRepliesForAnswer(answerId);
        assert replies.size() == 0 : "Reply not deleted properly.";
        
        // Reset
        dao.deleteAnswer(answerId);
        dao.deleteQuestion(questionId);
        System.out.println("Reply deleting test complete.");
    }

    /**
    * Print out the results of the test
    * 
    * @since     1.0
    */
    private static void printTestResults() {
        System.out.println("Testing is now complete.");
        System.out.println("________________________________________");
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