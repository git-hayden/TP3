package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//manages the collection of messages
public class Messages {
    private List<Message> messageList;
    
    // constructor
    public Messages() {
        this.messageList = new ArrayList<>();
    }
    
    // constructor with list of messages
    public Messages(List<Message> messages) {
        this.messageList = new ArrayList<>(messages);
    }
    
    // add a message
    public void addMessage(Message message) {
        messageList.add(message);
    }
    
    // get all messages
    public List<Message> getAllMessages() {
        return new ArrayList<>(messageList);
    }
    
    // search by message id
    public Message getMessageById(int messageId) {
        return messageList.stream()
            .filter(q -> q.getMessageId() == messageId)
            .findFirst()
            .orElse(null);
    }
    
    
    // remove message
    public boolean deleteMessage(int messageId) {
        return messageList.removeIf(q -> q.getMessageId() == messageId);
    }
    
    // author filter
    public Messages filterByAuthor(String authorUserName) {
        List<Message> filtered = messageList.stream()
            .filter(q -> authorUserName.equals(q.getAuthorUserName()))
            .collect(Collectors.toList());
        return new Messages(filtered);
    }
    
    
    // get count of messages
    public int size() {
        return messageList.size();
    }
    
    // check if empty
    public boolean isEmpty() {
        return messageList.isEmpty();
    }
    
    // clear all messages
    public void clear() {
        messageList.clear();
    }
}