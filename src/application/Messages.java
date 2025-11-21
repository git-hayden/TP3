package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p> Title: Messages Class. </p>
 * 
 * <p> Description: An initializer for a collection of Message objects. </p>
 * 
 */

//manages the collection of messages
public class Messages {
    private List<Message> messageList;
    
    // constructor
	/**
	  * This method creates a new Messages object.
	  * Creates an empty ArrayList
	  */
    public Messages() {
        this.messageList = new ArrayList<>();
    }
    
    // constructor with list of messages
	/**
	  * This method creates a new Messages object.
	  * Then saves passed message into the ArrayList.
	  * 
	  * @param messages		the message object to be passed into the ArrayList.
	  */
    public Messages(List<Message> messages) {
        this.messageList = new ArrayList<>(messages);
    }
	/**
	  * This method adds a single message to the ArrayList.
	  * 
	  * @param messages		the message object to be passed into the ArrayList.
	  */
    // add a message
    
    public void addMessage(Message message) {
        messageList.add(message);
    }
    
    // get all messages
	/**
	  * This method gets all the messages from the ArrayList.
	  * 
	  */
    public List<Message> getAllMessages() {
        return new ArrayList<>(messageList);
    }
    
    // search by message id
	/**
	  * This method returns the message id.
	  * 
	  * @param messageId		the message id to be returned
	  */
    public Message getMessageById(int messageId) {
        return messageList.stream()
            .filter(q -> q.getMessageId() == messageId)
            .findFirst()
            .orElse(null);
    }
    
    
    // remove message
	/**
	  * This method removes a message from the ArrayList.
	  * 
	  * @param messageId		the message id to be removed from the messagesList.
	  */
    public boolean deleteMessage(int messageId) {
        return messageList.removeIf(q -> q.getMessageId() == messageId);
    }
    
    // author filter
	/**
	  * This method displays only messages from the authorUserName.
	  * 
	  * @param authorUserName		the username of the messages to be displayed.
	  */
    public Messages filterByAuthor(String authorUserName) {
        List<Message> filtered = messageList.stream()
            .filter(q -> authorUserName.equals(q.getAuthorUserName()))
            .collect(Collectors.toList());
        return new Messages(filtered);
    }
    
    
    // get count of messages
    /**
     * @return size of message list
     */
    public int size() {
        return messageList.size();
    }
    
    // check if empty
    /**
     * @return if list is empty
     */
    public boolean isEmpty() {
        return messageList.isEmpty();
    }
    
    // clear all messages
	/**
	  * This method clears the messageList.
	  * 
	  */
    public void clear() {
        messageList.clear();
    }
}