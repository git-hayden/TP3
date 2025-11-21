package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import databasePart1.MessagesDAO;

import java.sql.SQLException;

/*******
 * <p> Title: MessagingPage Class. </p>
 * 
 * <p> Description: A page for private messaging functionality. </p>
 * 
 */

public class MessagingPage {
	private Stage stage;
    private String currentUserName;
    private String currentUserRole;
    private MessagesDAO dao;
    //UI components
    private ListView<Message> inboxListView;
    private TextArea messageDetailArea;
    
    //currently selected message
    private Message selectedMessage;
	/**
	  * This method creates a MessagingPage object.
	  * 
	  * @param stage			creation of a new stage for messaging page
	  * @param currentUserName	the username of the current user
	  * @param currentUserRole	the role of the current user
	  * 
	  */
    public MessagingPage(Stage stage, String currentUserName, String currentUserRole) {
        this.stage = stage;
        this.currentUserName = currentUserName;
        this.currentUserRole = currentUserRole;
        
        try {
            this.dao = new MessagesDAO();
        } catch (SQLException e) {
            showError("Failed to connect to the database");
        }
    }
    
    //create the scene for UI
	/**
	  * This method creates the brand new scene, includes left view panel, center message detail,
	  * and on the right side the buttons.
	  * 
	  * @return Creates brand new scene
	  */
    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        //left: message list
        mainLayout.setLeft(createMessagesSection());

        //center: message details
        mainLayout.setCenter(createDetailSection());

        //right: action buttons.
        mainLayout.setRight(createActionSection());

        return new Scene(mainLayout, 1200, 800);
    }
    
    //Create left side with message list
	/**
	  * Creation of the left side message view panel. Shows the title of message in inbox.
	  * 
	  * @return The messages box on the left hand side.
	  */
    private VBox createMessagesSection() {
        VBox messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(10));
        messagesBox.setPrefWidth(350);

        Label messageLabel = new Label("Messages");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        inboxListView = new ListView<>();
        inboxListView.setPrefHeight(600);

        //cell factory for message list    
        inboxListView.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else{
                    setText(message.getTitle()+ " (" + message.getAuthorUserName() + ")");
    
                }
            }
        });

        inboxListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> displayMessageDetail(newVal)); 

        loadMessages();
        messagesBox.getChildren().addAll(messageLabel, inboxListView);
        return messagesBox;
    }
    
    //create center section with message details
	/**
	  * This method creates the center view with more detailed message information.
	  * Includes title, who sent message, time, and message content.
	  * 
	  * 
	  * @return View of the details of single message that is clicked.
	  */
    private VBox createDetailSection() {
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));

        //question detail
        Label detailLabel = new Label("Message Details");
        detailLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        messageDetailArea = new TextArea();
        messageDetailArea.setEditable(false);
        messageDetailArea.setPrefHeight(200);
        messageDetailArea.setWrapText(true);

        detailBox.getChildren().addAll(detailLabel, messageDetailArea);
        return detailBox;
}
  //create right section with action buttons
	/**
	  * This method creates the buttons on the right hand side of the message page.
	  * Buttons include Create message, Reply, Refresh, and Back.
	  * 
	  * @return Four buttons on the right hand side of page.
	  */
    private VBox createActionSection() {
        VBox actionBox = new VBox(10);
        actionBox.setPadding(new Insets(10));
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPrefWidth(200);

        //add message button
        Button addMessageBtn = new Button("Create message");
        addMessageBtn.setPrefWidth(180);
        addMessageBtn.setOnAction(e -> createMessage());
        //add reply button
        Button addReplyBtn = new Button("Reply");
        addReplyBtn.setPrefWidth(180);
        addReplyBtn.setOnAction(e -> addReply());
        //refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefWidth(180);
        refreshBtn.setOnAction(e -> refreshData());
        //back button
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(180);
        backBtn.setOnAction(e -> goBack());
        actionBox.getChildren().addAll(
            addMessageBtn, addReplyBtn, refreshBtn, backBtn
        );
        return actionBox;
    }
    
    //create a message
	/**
	  * This method gets the information to create the brand new message.
	  * Then passes the information into createMessage located in MessagesDAO database
	  * 
	  */
    private void createMessage() {
      //dialog for creating a message
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setTitle("Create Message");
      dialog.setHeaderText("Enter the details of the message");

      GridPane grid = new GridPane();
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(10));
      
      TextField sendField = new TextField();
      sendField.setPromptText("Send to:");
      TextField titleField = new TextField();
      titleField.setPromptText("Enter the title of the message");
      TextArea contentField = new TextArea();
      contentField.setPromptText("Enter the content of the message");
      contentField.setPrefRowCount(5);
      contentField.setWrapText(true);
      
      grid.add(new Label("Username:"), 0, 0);
      grid.add(sendField, 1, 0);
      grid.add(new Label("Title:"), 0, 1);
      grid.add(titleField, 1, 1);
      grid.add(new Label("Content:"), 0, 2);
      grid.add(contentField, 1, 2);
  
      dialog.getDialogPane().setContent(grid);
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

      dialog.showAndWait().ifPresent(response -> {
          if (response == ButtonType.OK) {
              String title = titleField.getText();
              String content = contentField.getText();
              String receiver = sendField.getText().trim();
              //create the message
              Message newMessage = new Message(title.trim(), content.trim(), currentUserName, receiver);

              try {
                  dao.createMessage(newMessage);
                  showInfo("Message created successfully!");
                  refreshData();
              } catch (SQLException e) {
                  showError("Failed to create question: " + e.getMessage());
              }
          }
      });
  }
    
    //load message
	/**
	  * This method loads only the messages where the current user is the receiver.
	  * Loads both the messages list as well as details.
	  * 
	  */
    private void loadMessages() {
        try {
            Messages messages = dao.getUserMessages(currentUserName);
            ObservableList<Message> messageList = FXCollections.observableArrayList(messages.getAllMessages());
            inboxListView.setItems(messageList);
        } catch (SQLException e) { showError("Failed to load messages: " + e.getMessage());}
    }
        //display message detail
        private void displayMessageDetail(Message message) {
            selectedMessage = message;
            if (selectedMessage == null) {
                messageDetailArea.clear();
                inboxListView.setItems(FXCollections.observableArrayList());
                return;
            }
            String details = "Title: " + message.getTitle() + "\n\n" +
            "Author: " + message.getAuthorUserName() + "\n" +
            "Created At: " + message.getCreatedAt().toLocalDate() + "\n" +
            "Content:\n" + message.getContent();
            messageDetailArea.setText(details);

    }
    //add an reply
    /**
   	  * This method lets you reply to the currently selected message.
   	  * 
   	  */
    private void addReply() {
    	if (selectedMessage == null) {
                showError("Please select a message to reply to");
                return;
            }
            //dialog for adding an reply
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reply to message");
            dialog.setHeaderText("Add response to: " + selectedMessage.getTitle());
            dialog.setContentText("Enter the content of the message");

            dialog.showAndWait().ifPresent(response -> {
            
                Message newMessage = new Message(selectedMessage.getTitle(), response.trim(), currentUserName, selectedMessage.getAuthorUserName());
                try {
                    dao.createMessage(newMessage);
                    showInfo("Reply added successfully!");
                    displayMessageDetail(selectedMessage);
                } catch (SQLException e) {
                    showError("Failed to add answer: " + e.getMessage());
                }
            });
        }
	//Show error and info messages
	/**
	  * This method shows any errors that happen with invalid inputs.
	  * 
	  * @param message		message to be checked for error
	  * 
	  * @return The results of the error
	  */
	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(message);
		alert.showAndWait();
	}
	/**
	  * This method shows information of the message
	  * 
	  * @param message		message to be checked for info
	  * 
	  * @return The results of the information of message
	  */
	private void showInfo(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setContentText(message);
		alert.showAndWait();
}
    //refresh data
	/**
	  * This method refreshes the messages inbox.
	  * 
	  */
    private void refreshData() {
        loadMessages();
        if(selectedMessage != null) {
            try {
                Message refreshed = dao.getMessageById(selectedMessage.getMessageId());
                displayMessageDetail(refreshed);
            }catch (SQLException e) {displayMessageDetail(null);}
        }
    }

    //navigate to home page for role
	/**
	  * This method exits the inbox and navigates back to the home page.
	  * 
	  */
    private void goBack() {
    	if(currentUserRole.equals("Admin")) {
    		AdminHomePage adminHomePage = new AdminHomePage(stage,currentUserName);
    		stage.setScene(adminHomePage.createScene());
    	} else {
    		UserHomePage userHomePage = new UserHomePage(stage,currentUserName);
    		stage.setScene(userHomePage.createScene());
    	}
}
}
