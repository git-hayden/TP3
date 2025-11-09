package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {
    private Stage stage;
    private String userName;
    private User currentUser;
    private DatabaseHelper databaseHelper;
    
    //constructor
    public UserHomePage(Stage stage, String userName, User currentUser, DatabaseHelper databaseHelper) {
        this.stage = stage;
        this.userName = userName;
        this.currentUser = currentUser;
        this.databaseHelper = databaseHelper;
    }
    
    //create the scene
    public Scene createScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        
        //label to display Hello user
        Label userLabel = new Label("Hello, User " + userName + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        //discussion board button
        Button discussionBoardBtn = new Button("Discussion Board");
        discussionBoardBtn.setPrefWidth(200);
        discussionBoardBtn.setOnAction(e -> {
            DisussionBoardPage dbPage = new DisussionBoardPage(stage, userName, "User", databaseHelper);
            stage.setScene(dbPage.createScene());
        });
        layout.getChildren().addAll(userLabel, discussionBoardBtn);
        
        //reviewer request button (only shown to users who are not reviewers)
        if ("User".equalsIgnoreCase(currentUser.getRole())) {
            if(currentUser.isRequestPending()) {
                Label pendingLabel = new Label("⏳ Reviewer Request Pending");
                pendingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: orange;");
                layout.getChildren().add(pendingLabel);
            } else {
                Button requestBtn = new Button("Request Reviewer Permission");
                requestBtn.setPrefWidth(200);
                requestBtn.setOnAction(e -> {
                    try {
                        currentUser.setReviewerRequestPending(true);
                        databaseHelper.setReviewerRequestPending(currentUser.getUserName(), true);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            "Request submitted! An instructor will review your work.");
                        alert.showAndWait();

                        stage.setScene(createScene());
                    } catch (SQLException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Failed to submit request. Please try again.");
                        alert.showAndWait();
                    }
                });
                layout.getChildren().add(requestBtn);
            }
        } else if ("Reviewer".equalsIgnoreCase(currentUser.getRole())) {
            Label reviewerLabel = new Label("✓ You are a Reviewer");
            reviewerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
            layout.getChildren().add(reviewerLabel);
        }
        
        return new Scene(layout, 800, 400);
    }

    public void show(Stage primaryStage) {
	    // Set the scene to primary stage
	    primaryStage.setScene(createScene());
	    primaryStage.setTitle("User Page");
    }
}