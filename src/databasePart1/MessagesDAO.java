package databasePart1;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import application.Message;
import application.Messages;

/*******
 * <p> Title: MessagesDAO Class. </p>
 * 
 * <p> Description: A database to handle message functionality. </p>
 * 
 */

//data access object for the message
public class MessagesDAO {
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
    public MessagesDAO() throws SQLException {
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
	/**
	  * This method creates the table to store the messages.
	  * It also stores the message id, title, content, username of sender/receiver
	  * and time stamps of when message was created.
	  */
    private void createTables() throws SQLException {
    	//Clear table comment out if not needed
        //statement.execute("DROP TABLE IF EXISTS messages");

        //messages table.
        String messageTable = "CREATE TABLE IF NOT EXISTS messages(" +
        "messageId INT AUTO_INCREMENT PRIMARY KEY," +
        "title VARCHAR(255) NOT NULL," +
        "content TEXT NOT NULL," +
        "authorUserName VARCHAR(255) NOT NULL," +
        "receiverUserName VARCHAR(255) NOT NULL," +
        "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        statement.execute(messageTable);
    }
    //insert a message 
	/**
	  * This method creates a message, and inserts the data into the messages table.
	  * 
	  * @param message			the content of the message to be sent.
	  * 
	  */
    public int createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (title, content, authorUserName, receiverUserName) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, message.getTitle());
            pstmt.setString(2, message.getContent());
            pstmt.setString(3, message.getAuthorUserName());
            pstmt.setString(4, message.getReceiverUserName());
            pstmt.executeUpdate();
            //return the message id
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                message.setMessageId(generatedId);
                return generatedId;
            }
        }
            return -1;
        }
        //get all users messages
    	/**
    	  * This method displays only the messages from table that match their username.
	 	  * 
	 	  * @param userName		userName to display the messages of
	 	  * 
	 	  */
        public Messages getUserMessages(String userName) throws SQLException {
            Messages messages = new Messages();
            String sql = "SELECT * FROM messages WHERE receiverUserName = ? ORDER BY createdAt DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            	 pstmt.setString(1, userName);
                 ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Message m = extractMessageFromResultSet(rs);
                    messages.addMessage(m);
                }
            }
            return messages;
        }
    	/**
    	  * This method returns the message id.
    	  * 
    	  * @param messageId		id of the message to be returned
   	  	  * 
   	  	  */
        public Message getMessageById(int messageId) throws SQLException {
            String sql = "SELECT * FROM messages WHERE messageId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, messageId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return extractMessageFromResultSet(rs);
                }
            }
            return null;
        }
        //helper methods for all operations
        private Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
            Message m = new Message(
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("authorUserName"),
                rs.getString("receiverUserName")
            );
            m.setMessageId(rs.getInt("messageId"));
            m.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            m.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
            return m;
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
