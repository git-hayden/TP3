package application;

import java.time.LocalDateTime;

/*******
 * <p> Title: Message Class. </p>
 * 
 * <p> Description: An initializer for a single Message object. </p>
 * 
 */

//create a message class
public class Message {
    private int messageId;
    private String title;
    private String content;
    private String authorUserName;
    private String receiverUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //constructor getter and setter
	/**
	  * This method creates a Message object.
	  * 
	  * @param title			the title that summarizes the content of message
	  * @param content			the message content to be sent
	  * @param authorUserName	the username of the sender of message
	  * @param receiverUserName	the username of the receiver of message
	  * 
	  */
    public Message(String title, String content, String authorUserName, String receiverUserName) {
        this.title = title;
        this.content = content;
        this.authorUserName = authorUserName;
        this.receiverUserName = receiverUserName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    //getters and setters
    /**
     * 
     * @param title title of message
     */
    public String getTitle() {
        return title;
    }
    /**
     * 
     * @param content content of message
     */
    public String getContent() {
        return content;
    }
    /**
     * 
     * @param authorUserName userName of sender
     */
    public String getAuthorUserName() {
        return authorUserName;
    }
    /**
     * 
     * @param receiverUserName userName of receiver
     */
    public String getReceiverUserName() {
    	return receiverUserName;
    }
    /**
     * 
     * @param messageId id of message
     */
    public int getMessageId() {
        return messageId;
    }
    /**
     * 
     * @param createdAt time of message creation
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    /**
     * 
     * @param updatedAt time message updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    /**
     * @return title of message, and updated time
     */
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now(); //update the updatedAt time
    }
    /**
     * @return content of message, and updated time
     */
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now(); //update the updatedAt time
    }
    /**
     * @return username of sender
     */
    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }
    /**
     * @return username of receiver
     */
    public void setRecieverUserName(String receiverUserName) {
    	this.receiverUserName = receiverUserName;
    }
    /**
     * @return time message created at
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    /**
     * @return time message updated at
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    /**
     * @return message id
     */
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }  

    //display the message
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", title='" + title + '\'' +
                ", authorUserName='" + authorUserName + '\'' +
                ", recieverUserName='" + receiverUserName + '\'' +
                '}';
    }
}