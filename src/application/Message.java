package application;

import java.time.LocalDateTime;


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
    public Message(String title, String content, String authorUserName, String receiverUserName) {
        this.title = title;
        this.content = content;
        this.authorUserName = authorUserName;
        this.receiverUserName = receiverUserName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    //getters and setters
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getAuthorUserName() {
        return authorUserName;
    }
    public String getReceiverUserName() {
    	return receiverUserName;
    }
    public int getMessageId() {
        return messageId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now(); //update the updatedAt time
    }
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now(); //update the updatedAt time
    }
    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }
    public void setRecieverUserName(String receiverUserName) {
    	this.receiverUserName = receiverUserName;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
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