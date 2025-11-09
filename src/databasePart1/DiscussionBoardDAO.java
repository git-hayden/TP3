package databasePart1;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import application.Question;
import application.Answer;
import application.Questions;
import application.Answers;
import application.Reply;
import application.Replies;
import application.ReviewerWeight;

//data access object for the discussion board
public class DiscussionBoardDAO {
    private Connection connection;
    private Statement statement;

    //db credentials (from DatabaseHelper)

    // JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

    //constructor
    public DiscussionBoardDAO() throws SQLException {
        connectToDatabase();
    }
    //connect to db 
    private void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            createTables();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to connect to the database", e);
        }
    }
    //create the tables
    private void createTables() throws SQLException {
        //questions table.
        String questionsTable = "CREATE TABLE IF NOT EXISTS questions(" +
        "questionId INT AUTO_INCREMENT PRIMARY KEY," +
        "title VARCHAR(255) NOT NULL," +
        "content TEXT NOT NULL," +
        "authorUserName VARCHAR(255) NOT NULL," +
        "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "isAnswered BOOLEAN DEFAULT FALSE," +
        "category VARCHAR(100))";
        statement.execute(questionsTable);
    
    //answers table.
    String answersTable = "CREATE TABLE IF NOT EXISTS answers(" +
    "answerId INT AUTO_INCREMENT PRIMARY KEY," +
    "questionId INT NOT NULL," +
    "content TEXT NOT NULL," +
    "authorUserName VARCHAR(255) NOT NULL," +
    "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
    "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
    "isAccepted BOOLEAN DEFAULT FALSE," +
    "isCorrect BOOLEAN DEFAULT FALSE," +
    "FOREIGN KEY (questionId) REFERENCES questions(questionId))";

    statement.execute(answersTable);
    
    //replies table.
    String repliesTable = "CREATE TABLE IF NOT EXISTS replies(" +
    "replyId INT AUTO_INCREMENT PRIMARY KEY," +
    "answerId INT NOT NULL," +
    "content TEXT NOT NULL," +
    "authorUserName VARCHAR(255) NOT NULL," +
    "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
    "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
    "FOREIGN KEY (answerId) REFERENCES answers(answerId))";

    statement.execute(repliesTable);

    //reviewer_weights table.
    String reviewerWeightsTable = "CREATE TABLE IF NOT EXISTS reviewer_weights(" +
    "reviewerWeightId INT AUTO_INCREMENT PRIMARY KEY," +
    "studentUserName VARCHAR(255) NOT NULL," +
    "reviewerUserName VARCHAR(255) NOT NULL," +
    "weight DOUBLE DEFAULT 1.0," +
    "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
    "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
    "CONSTRAINT unique_reviewer UNIQUE (studentUserName, reviewerUserName))";

    statement.execute(reviewerWeightsTable);
    }
    //insert a question 
    public int createQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions (title, content, authorUserName, category) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, question.getTitle());
            pstmt.setString(2, question.getContent());
            pstmt.setString(3, question.getAuthorUserName());
            pstmt.setString(4, question.getCategory());
            pstmt.executeUpdate();
            //return the question id
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                question.setQuestionId(generatedId);
                return generatedId;
            }
        }
            return -1;
        }
        //get all questions
        public Questions getAllQuestions() throws SQLException {
            Questions questions = new Questions();
            String sql = "SELECT * FROM questions ORDER BY createdAt DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    Question q = extractQuestionFromResultSet(rs);
                    questions.addQuestion(q);
                }
            }
            return questions;
        }
        //get question by id
        public Question getQuestionById(int questionId) throws SQLException {
            String sql = "SELECT * FROM questions WHERE questionId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, questionId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return extractQuestionFromResultSet(rs);
                }
            }
            return null;
        }
        //update a question
        public boolean updateQuestion(Question question) throws SQLException {
            String sql = "UPDATE questions SET title = ?, content = ?, updatedAt = ?, "
                    + "isAnswered = ?, category = ? WHERE questionId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, question.getTitle());
                pstmt.setString(2, question.getContent());
                pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setBoolean(4, question.getIsAnswered());
                pstmt.setString(5, question.getCategory());
                pstmt.setInt(6, question.getQuestionId());
                return pstmt.executeUpdate() > 0;
            }
        }
        //delete a question
        public boolean deleteQuestion(int questionId) throws SQLException {
            String sql = "DELETE FROM questions WHERE questionId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, questionId);
                return pstmt.executeUpdate() > 0;
            }
        }

        //ANSWER CRUD OPERATIONS

        //insert an answer
        public int createAnswer(Answer answer) throws SQLException {
            String sql = "INSERT INTO answers (questionId, content, authorUserName, createdAt, updatedAt, isAccepted) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, answer.getQuestionId());
                pstmt.setString(2, answer.getContent());
                pstmt.setString(3, answer.getAuthorUserName()); 
                pstmt.setTimestamp(4, Timestamp.valueOf(answer.getCreatedAt()));
                pstmt.setTimestamp(5, Timestamp.valueOf(answer.getUpdatedAt()));
                pstmt.setBoolean(6, answer.getIsAccepted());
                
                pstmt.executeUpdate();
                
                // generate answerId
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    answer.setAnswerId(generatedId);
                    return generatedId;
                }
            }
            return -1;
        }
        //get all answers for a question
        public Answers getAnswersForQuestion(int questionId) throws SQLException {
            Answers answers = new Answers();
            String sql = "SELECT * FROM answers WHERE questionId = ? ORDER BY isAccepted DESC, createdAt ASC";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, questionId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Answer a = extractAnswerFromResultSet(rs);
                    answers.addAnswer(a);
                }
            }
            return answers;
        }

        /**
         * Retrieves answers for a question sorted by weighted score.
         * Answers are sorted based on a score calculated as:
         * (1.0 + bonuses) * reviewer_weight
         * where bonuses include +100 for accepted, +50 for helpful.
         *
         * @param questionId the ID of the question to get answers for
         * @param studentUserName the student viewing the answers (for personalized weights)
         * @return Answers object containing all answers sorted by weighted score
         * @throws SQLException if database access error occurs
         */
        public Answers getWeightedAnswersForQuestion(int questionId, String studentUserName) throws SQLException {
            Answers answers = new Answers();

            //join answers with reviewer weights to calculate weighted scores
            String sql = "SELECT a.*, COALESCE(rw.weight, 1.0) as reviewerWeight " +
                        "FROM answers a " +
                        "LEFT JOIN reviewer_weights rw ON a.authorUserName = rw.reviewerUserName " +
                        "AND rw.studentUserName = ? " +
                        "WHERE a.questionId = ? " +
                        "ORDER BY (1.0 + CASE WHEN a.isAccepted THEN 100 ELSE 0 END + " +
                        "CASE WHEN a.isCorrect THEN 50 ELSE 0 END) * COALESCE(rw.weight, 1.0) DESC, " +
                        "a.createdAt ASC";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, studentUserName);
                pstmt.setInt(2, questionId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Answer a = extractAnswerFromResultSet(rs);
                    answers.addAnswer(a);
                }
            }
            return answers;
        }
        //get all answers
        public Answers getAllAnswers() throws SQLException {
            Answers answers = new Answers();
            String sql = "SELECT * FROM answers ORDER BY createdAt DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Answer a = extractAnswerFromResultSet(rs);
                    answers.addAnswer(a);
                }
            }
            return answers;
        }
        //update an answer
        public boolean updateAnswer(Answer answer) throws SQLException {
            String sql = "UPDATE answers SET content = ?, updatedAt = ?, isAccepted = ?, isCorrect = ? WHERE answerId = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, answer.getContent());
                pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setBoolean(3, answer.getIsAccepted());
                pstmt.setBoolean(4, answer.isCorrect());
                pstmt.setInt(5, answer.getAnswerId());
                
                return pstmt.executeUpdate() > 0;
            }
        }
        //delete an answer
        public boolean deleteAnswer(int answerId) throws SQLException {
            String sql = "DELETE FROM answers WHERE answerId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, answerId);
                return pstmt.executeUpdate() > 0;
            }
        }
        //get answer by id
        public Answer getAnswerById(int answerId) throws SQLException {
            String sql = "SELECT * FROM answers WHERE answerId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, answerId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return extractAnswerFromResultSet(rs);
                }
            }
            return null;
        }
        
        //REPLY CRUD OPERATIONS

        //insert a reply
        public int createReply(Reply reply) throws SQLException {
            String sql = "INSERT INTO replies (answerId, content, authorUserName, createdAt, updatedAt) "
                    + "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, reply.getAnswerId());
                pstmt.setString(2, reply.getContent());
                pstmt.setString(3, reply.getAuthorUserName()); 
                pstmt.setTimestamp(4, Timestamp.valueOf(reply.getCreatedAt()));
                pstmt.setTimestamp(5, Timestamp.valueOf(reply.getUpdatedAt()));
                
                pstmt.executeUpdate();
                
                // generate replyId
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    reply.setReplyId(generatedId);
                    return generatedId;
                }
            }
            return -1;
        }
        //get all replies for an answer
        public Replies getRepliesForAnswer(int answerId) throws SQLException {
            Replies replies = new Replies();
            String sql = "SELECT * FROM replies WHERE answerId = ? ORDER BY createdAt ASC";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, answerId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Reply r = extractReplyFromResultSet(rs);
                    replies.addReply(r);
                }
            }
            return replies;
        }
        //get all replies
        public Replies getAllReplies() throws SQLException {
            Replies replies = new Replies();
            String sql = "SELECT * FROM answers ORDER BY createdAt DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reply r = extractReplyFromResultSet(rs);
                    replies.addReply(r);
                }
            }
            return replies;
        }
        //update a reply
        public boolean updateReply(Reply reply) throws SQLException {
            String sql = "UPDATE replies SET content = ?, updatedAt = ? WHERE replyId = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, reply.getContent());
                pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setInt(3, reply.getReplyId());
                
                return pstmt.executeUpdate() > 0;
            }
        }
        //delete a reply
        public boolean deleteReply(int replyId) throws SQLException {
            String sql = "DELETE FROM replies WHERE replyId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, replyId);
                return pstmt.executeUpdate() > 0;
            }
        }
        //helper methods for all operations
        private Question extractQuestionFromResultSet(ResultSet rs) throws SQLException {
            Question q = new Question(
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("authorUserName")
            );
            q.setQuestionId(rs.getInt("questionId"));
            q.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            q.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
            q.setIsAnswered(rs.getBoolean("isAnswered"));
            q.setCategory(rs.getString("category"));
            return q;
        }
        // extract an answer from the result set
        private Answer extractAnswerFromResultSet(ResultSet rs) throws SQLException {
            Answer a = new Answer(
                rs.getInt("answerId"),
                rs.getInt("questionId"),
                rs.getString("content"),
                rs.getString("authorUserName"),
                rs.getTimestamp("createdAt").toLocalDateTime(),
                rs.getTimestamp("updatedAt").toLocalDateTime(),
                rs.getBoolean("isAccepted")
            );
            a.setCorrect(rs.getBoolean("isCorrect"));
            return a;
        }
        // extract a reply from the result set
        private Reply extractReplyFromResultSet(ResultSet rs) throws SQLException {
            Reply r = new Reply(
                rs.getInt("replyId"),
                rs.getInt("answerId"),
                rs.getString("content"),
                rs.getString("authorUserName"),
                rs.getTimestamp("createdAt").toLocalDateTime(),
                rs.getTimestamp("updatedAt").toLocalDateTime()
            );
            return r;
        }

        //REVIEWER WEIGHT CRUD OPERATIONS

        /**
         * Sets or updates the weight assigned by a student to a reviewer.
         * Uses upsert logic: updates existing weight or inserts new if not present.
         *
         * @param studentUserName the student assigning the weight
         * @param reviewerUserName the reviewer being assigned a weight
         * @param weight the trust level value (typically 0.1 to 5.0)
         * @return true if operation was successful
         * @throws SQLException if database access error occurs
         */
        public boolean setReviewerWeight(String studentUserName, String reviewerUserName, double weight) throws SQLException {
            //try to update first
            String updateSql = "UPDATE reviewer_weights SET weight = ?, updatedAt = ? WHERE studentUserName = ? AND reviewerUserName = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setDouble(1, weight);
                pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(3, studentUserName);
                pstmt.setString(4, reviewerUserName);

                if (pstmt.executeUpdate() > 0) {
                    return true; //update successful
                }
            }

            //if no rows updated, insert new
            String insertSql = "INSERT INTO reviewer_weights (studentUserName, reviewerUserName, weight) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                pstmt.setString(1, studentUserName);
                pstmt.setString(2, reviewerUserName);
                pstmt.setDouble(3, weight);
                return pstmt.executeUpdate() > 0;
            }
        }

        /**
         * Retrieves the weight assigned by a student to a specific reviewer.
         *
         * @param studentUserName the student who assigned the weight
         * @param reviewerUserName the reviewer to get the weight for
         * @return the weight value, or 1.0 if no weight is set (default)
         * @throws SQLException if database access error occurs
         */
        public Double getReviewerWeight(String studentUserName, String reviewerUserName) throws SQLException {
            String sql = "SELECT weight FROM reviewer_weights WHERE studentUserName = ? AND reviewerUserName = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, studentUserName);
                pstmt.setString(2, reviewerUserName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("weight");
                }
            }
            return 1.0; //default weight if not set
        }

        /**
         * Retrieves all reviewer weights assigned by a specific student.
         * Results are ordered by weight in descending order (highest trust first).
         *
         * @param studentUserName the student whose weights to retrieve
         * @return list of ReviewerWeight objects
         * @throws SQLException if database access error occurs
         */
        public List<ReviewerWeight> getAllReviewerWeights(String studentUserName) throws SQLException {
            List<ReviewerWeight> weights = new ArrayList<>();
            String sql = "SELECT * FROM reviewer_weights WHERE studentUserName = ? ORDER BY weight DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, studentUserName);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    ReviewerWeight rw = extractReviewerWeightFromResultSet(rs);
                    weights.add(rw);
                }
            }
            return weights;
        }

        /**
         * Deletes the weight assigned by a student to a reviewer.
         * After deletion, the reviewer's weight will return to default (1.0).
         *
         * @param studentUserName the student who assigned the weight
         * @param reviewerUserName the reviewer whose weight to delete
         * @return true if a weight was deleted, false if none existed
         * @throws SQLException if database access error occurs
         */
        public boolean deleteReviewerWeight(String studentUserName, String reviewerUserName) throws SQLException {
            String sql = "DELETE FROM reviewer_weights WHERE studentUserName = ? AND reviewerUserName = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, studentUserName);
                pstmt.setString(2, reviewerUserName);
                return pstmt.executeUpdate() > 0;
            }
        }

        //extract reviewer weight from result set
        private ReviewerWeight extractReviewerWeightFromResultSet(ResultSet rs) throws SQLException {
            return new ReviewerWeight(
                rs.getInt("reviewerWeightId"),
                rs.getString("studentUserName"),
                rs.getString("reviewerUserName"),
                rs.getDouble("weight"),
                rs.getTimestamp("createdAt").toLocalDateTime(),
                rs.getTimestamp("updatedAt").toLocalDateTime()
            );
        }

        //finally, close the connection
        public void closeConnection() {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
