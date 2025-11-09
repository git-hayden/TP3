package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class ReviewerManagamentPage {
    private Stage stage;
    private String adminUserName;
    private DatabaseHelper databaseHelper;
    private Questions allQuestions;
    private Answers allAnswers;

    public ReviewerManagamentPage(Stage stage, String adminUserName, DatabaseHelper databaseHelper, Questions questions, Answers answers) {
        this.stage = stage;
        this.adminUserName = adminUserName;
        this.databaseHelper = databaseHelper;
        this.allQuestions = questions;
        this.allAnswers = answers;
    }

    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        Label title = new Label("Reviewer Permission Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        mainLayout.setTop(title);
        BorderPane.setAlignment(title, javafx.geometry.Pos.CENTER);

        //fetch pending reviewer requests
        List<User> pendingUsers;
        try {
            pendingUsers = databaseHelper.getUsersWithReviewerRequestPending();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to fetch pending reviewer requests: " + e.getMessage()).showAndWait();
            return new Scene(new VBox(), 800, 500);
        }
        
        ListView<User> userList = new ListView<>();
        userList.getItems().addAll(pendingUsers);
        userList.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? null : user.getUserName());
            }
        });

        //details
        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(10));
        ScrollPane contentScroll = new ScrollPane();
        HBox buttonBox = new HBox(10);
        Button approveBtn = new Button("Make Reviewer");
        approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        approveBtn.setDisable(true);
        Button rejectBtn = new Button("Reject Request");
        rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        rejectBtn.setDisable(true);
        buttonBox.getChildren().addAll(approveBtn, rejectBtn);
        detailsBox.getChildren().addAll(new Label("Select a user to review:"), contentScroll, buttonBox);

        //listener for user selection
        userList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, user) -> {
            if (user != null) {
                VBox content = new VBox(10);

                Questions userQuestions = allQuestions.filterByAuthor(user.getUserName());
                content.getChildren().add(new Label("Questions posted: " + userQuestions.size()));
                for (Question q : userQuestions.getAllQuestions()) {
                    TextArea ta = new TextArea(q.getTitle() + "\n" + q.getContent());
                    ta.setEditable(false);
                    ta.setPrefRowCount(3);
                    content.getChildren().add(ta);
        }

        Answers userAnswers = allAnswers.filterByAuthor(user.getUserName());
        content.getChildren().add(new Label("Answers posted: " + userAnswers.size()));
        for (Answer a : userAnswers.getAllAnswers()) {
            TextArea ta = new TextArea(a.getContent());
            ta.setEditable(false);
            ta.setPrefRowCount(2);
            content.getChildren().add(ta);
        }


        contentScroll.setContent(content);
        approveBtn.setDisable(false);
        rejectBtn.setDisable(false);

        approveBtn.setOnAction(e -> {
        try {
            databaseHelper.changeRoleToReviewer(user.getUserName());
            new Alert(Alert.AlertType.INFORMATION,
            user.getUserName() + " is now a reviewer.").showAndWait();
            stage.setScene(createScene());
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to make " + user.getUserName() + " a reviewer: " + ex.getMessage()).showAndWait();
        }
        });

        rejectBtn.setOnAction(e -> {
            try {
                databaseHelper.setReviewerRequestPending(user.getUserName(), false);
                new Alert(Alert.AlertType.INFORMATION, "Request rejected.").showAndWait();
                stage.setScene(createScene());
            } catch (SQLException ex) {
                new Alert(Alert.AlertType.ERROR, "Failed to reject request: " + ex.getMessage()).showAndWait();
            }
        });
    }
});

    SplitPane splitPane = new SplitPane(userList, detailsBox);
    splitPane.setDividerPositions(0.3);
    mainLayout.setCenter(splitPane);

    Button backBtn = new Button("Back to Admin Home");
    backBtn.setOnAction(e -> {
        AdminHomePage adminHomePage = new AdminHomePage(stage, adminUserName, databaseHelper);
        stage.setScene(adminHomePage.createScene());
    });

    mainLayout.setBottom(backBtn);
    BorderPane.setAlignment(backBtn, javafx.geometry.Pos.CENTER);
    BorderPane.setMargin(backBtn, new Insets(10));
    return new Scene(mainLayout, 1200, 800);
}
}