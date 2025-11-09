JUnit Tests for Reviewer Weight Feature
========================================

TEST FILE LOCATION:
test/databasePart1/ReviewerWeightDAOTest.java

TESTS INCLUDED (8 total):

1. testSetNewReviewerWeight()
   - Purpose: Verify new weight can be inserted
   - Tests: setReviewerWeight() and getReviewerWeight()

2. testUpdateExistingReviewerWeight()
   - Purpose: Verify upsert behavior (update existing weight)
   - Tests: setReviewerWeight() update functionality

3. testGetDefaultWeightForNonExistentReviewer()
   - Purpose: Verify default weight of 1.0 for unset reviewers
   - Tests: getReviewerWeight() default behavior

4. testDeleteReviewerWeight()
   - Purpose: Verify weight deletion works
   - Tests: deleteReviewerWeight()

5. testGetAllReviewerWeights()
   - Purpose: Verify student can retrieve all their weights
   - Tests: getAllReviewerWeights()

6. testSetMinimumWeight()
   - Purpose: Edge case - minimum weight (0.1)
   - Tests: Weight boundary validation

7. testSetMaximumWeight()
   - Purpose: Edge case - maximum weight (5.0)
   - Tests: Weight boundary validation

8. testMultipleStudentsDifferentWeights()
   - Purpose: Verify weight isolation between students
   - Tests: Per-student weight management

HOW TO RUN IN ECLIPSE:
======================

1. Download JUnit 4 JAR files:
   - junit-4.13.2.jar
   - hamcrest-core-1.3.jar

2. Add JUnit to project:
   - Right-click project → Properties → Java Build Path
   - Libraries tab → Add External JARs
   - Select the JUnit JAR files
   - Apply and Close

3. Run tests:
   - Right-click on ReviewerWeightDAOTest.java
   - Run As → JUnit Test
   - Green bar = all tests pass!

HOW TO RUN FROM COMMAND LINE:
==============================

javac -cp "junit-4.13.2.jar;hamcrest-core-1.3.jar;h2.jar;bin" -d bin test/databasePart1/ReviewerWeightDAOTest.java

java -cp "junit-4.13.2.jar;hamcrest-core-1.3.jar;h2.jar;bin" org.junit.runner.JUnitCore databasePart1.ReviewerWeightDAOTest

EXPECTED OUTPUT:
================
All 8 tests should pass with output like:
.........
Time: 0.5s
OK (8 tests)

NOTES:
======
- Tests use a test database to avoid affecting production data
- Each test cleans up after itself
- Tests can run in any order
- If a test fails, check the error message for details
