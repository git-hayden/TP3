package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import databasePart1.DatabaseHelper;
import databasePart1.DiscussionBoardDAO;
import java.sql.SQLException;
/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
    private Stage stage;
    private String userName;
    private DatabaseHelper databaseHelper;
    //constructor
    public AdminHomePage(Stage stage, String userName, DatabaseHelper databaseHelper) {
        this.stage = stage;
        this.userName = userName;
        this.databaseHelper = databaseHelper;
    }
    
    //create the scene
    public Scene createScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        
        //label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, Admin " + userName + "!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        //discussion board button
        Button discussionBoardBtn = new Button("Discussion Board");
        discussionBoardBtn.setPrefWidth(200);
        discussionBoardBtn.setOnAction(e -> {
            DisussionBoardPage dbPage = new DisussionBoardPage(stage, userName, "Admin", databaseHelper);
            stage.setScene(dbPage.createScene());
        });

        Button reviewerManagementBtn = new Button("Reviewer Management");
        reviewerManagementBtn.setPrefWidth(200);
        reviewerManagementBtn.setOnAction(e -> {
            try{
                DiscussionBoardDAO dao = new DiscussionBoardDAO();
                Questions questions = dao.getAllQuestions();
                Answers answers = dao.getAllAnswers();
                ReviewerManagamentPage reviewerManagementPage = new ReviewerManagamentPage(stage, userName, databaseHelper, questions, answers);
                stage.setScene(reviewerManagementPage.createScene());
            } catch (SQLException ex) {
                System.err.println("Database error: " + ex.getMessage());
                ex.printStackTrace();
            }

        });


        layout.getChildren().addAll(adminLabel, discussionBoardBtn, reviewerManagementBtn);
        return new Scene(layout, 800, 400);
    }
    
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
	    // Set the scene to primary stage
	    primaryStage.setScene(createScene());
	    primaryStage.setTitle("Admin Page");
    }
}