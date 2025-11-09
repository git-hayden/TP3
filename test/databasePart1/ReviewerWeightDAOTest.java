package databasePart1;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import application.ReviewerWeight;
import java.sql.SQLException;
import java.util.List;

/**
 * JUnit tests for reviewer weight functionality in DiscussionBoardDAO.
 * Tests the ability for students to set, retrieve, update, and delete
 * weightage values for reviewers.
 */
public class ReviewerWeightDAOTest {

    private DiscussionBoardDAO dao;
    private static final String TEST_STUDENT = "testStudent1";
    private static final String TEST_REVIEWER1 = "reviewer1";
    private static final String TEST_REVIEWER2 = "reviewer2";

    @Before
    public void setUp() throws SQLException {
        dao = new DiscussionBoardDAO();
        cleanupTestData();
    }

    @After
    public void tearDown() throws SQLException {
        cleanupTestData();
        if (dao != null) {
            dao.closeConnection();
        }
    }

    /**
     * Helper method to clean up test data before and after tests
     */
    private void cleanupTestData() throws SQLException {
        try {
            dao.deleteReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);
            dao.deleteReviewerWeight(TEST_STUDENT, TEST_REVIEWER2);
        } catch (SQLException e) {
            // Ignore if data doesn't exist
        }
    }

    /**
     * Test 1: Verify that a new reviewer weight can be set successfully.
     * Purpose: Ensure setReviewerWeight() inserts a new weight into the database.
     */
    @Test
    public void testSetNewReviewerWeight() throws SQLException {
        double expectedWeight = 3.5;

        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, expectedWeight);

        double actualWeight = dao.getReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);

        assertEquals("Weight should be set correctly", expectedWeight, actualWeight, 0.01);
    }

    /**
     * Test 2: Verify that updating an existing reviewer weight works.
     * Purpose: Ensure setReviewerWeight() updates (upsert behavior) when weight already exists.
     */
    @Test
    public void testUpdateExistingReviewerWeight() throws SQLException {
        double initialWeight = 2.0;
        double updatedWeight = 4.5;

        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, initialWeight);
        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, updatedWeight);

        double actualWeight = dao.getReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);

        assertEquals("Weight should be updated correctly", updatedWeight, actualWeight, 0.01);
    }

    /**
     * Test 3: Verify that getting a non-existent weight returns default value of 1.0.
     * Purpose: Ensure getReviewerWeight() returns 1.0 for reviewers without assigned weights.
     */
    @Test
    public void testGetDefaultWeightForNonExistentReviewer() throws SQLException {
        double defaultWeight = dao.getReviewerWeight(TEST_STUDENT, "nonExistentReviewer");

        assertEquals("Default weight should be 1.0", 1.0, defaultWeight, 0.01);
    }

    /**
     * Test 4: Verify that a reviewer weight can be deleted successfully.
     * Purpose: Ensure deleteReviewerWeight() removes the weight from the database.
     */
    @Test
    public void testDeleteReviewerWeight() throws SQLException {
        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, 3.0);

        dao.deleteReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);

        double weightAfterDelete = dao.getReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);
        assertEquals("Weight should return to default after deletion", 1.0, weightAfterDelete, 0.01);
    }

    /**
     * Test 5: Verify that getAllReviewerWeights() returns all weights for a student.
     * Purpose: Ensure a student can retrieve all their assigned reviewer weights.
     */
    @Test
    public void testGetAllReviewerWeights() throws SQLException {
        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, 2.5);
        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER2, 4.0);

        List<ReviewerWeight> weights = dao.getAllReviewerWeights(TEST_STUDENT);

        assertNotNull("Weights list should not be null", weights);
        assertEquals("Should have 2 reviewer weights", 2, weights.size());

        boolean found1 = false, found2 = false;
        for (ReviewerWeight rw : weights) {
            if (rw.getReviewerUserName().equals(TEST_REVIEWER1)) {
                assertEquals("Reviewer1 weight should be 2.5", 2.5, rw.getWeight(), 0.01);
                found1 = true;
            }
            if (rw.getReviewerUserName().equals(TEST_REVIEWER2)) {
                assertEquals("Reviewer2 weight should be 4.0", 4.0, rw.getWeight(), 0.01);
                found2 = true;
            }
        }
        assertTrue("Should find reviewer1 in results", found1);
        assertTrue("Should find reviewer2 in results", found2);
    }

    /**
     * Test 6: Verify that minimum weight value (0.1) can be set.
     * Purpose: Test edge case of minimum allowed weight.
     */
    @Test
    public void testSetMinimumWeight() throws SQLException {
        double minWeight = 0.1;

        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, minWeight);

        double actualWeight = dao.getReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);
        assertEquals("Minimum weight should be set correctly", minWeight, actualWeight, 0.01);
    }

    /**
     * Test 7: Verify that maximum weight value (5.0) can be set.
     * Purpose: Test edge case of maximum allowed weight.
     */
    @Test
    public void testSetMaximumWeight() throws SQLException {
        double maxWeight = 5.0;

        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, maxWeight);

        double actualWeight = dao.getReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);
        assertEquals("Maximum weight should be set correctly", maxWeight, actualWeight, 0.01);
    }

    /**
     * Test 8: Verify that different students can have different weights for the same reviewer.
     * Purpose: Ensure weight assignments are isolated per student.
     */
    @Test
    public void testMultipleStudentsDifferentWeights() throws SQLException {
        String student2 = "testStudent2";
        double student1Weight = 2.0;
        double student2Weight = 4.0;

        dao.setReviewerWeight(TEST_STUDENT, TEST_REVIEWER1, student1Weight);
        dao.setReviewerWeight(student2, TEST_REVIEWER1, student2Weight);

        double weight1 = dao.getReviewerWeight(TEST_STUDENT, TEST_REVIEWER1);
        double weight2 = dao.getReviewerWeight(student2, TEST_REVIEWER1);

        assertEquals("Student1's weight should be 2.0", student1Weight, weight1, 0.01);
        assertEquals("Student2's weight should be 4.0", student2Weight, weight2, 0.01);

        dao.deleteReviewerWeight(student2, TEST_REVIEWER1);
    }
}
