package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import databasePart1.DiscussionBoardDAO;
import java.sql.SQLException;

//UI for the discussion board
public class DiscussionBoardPage {
    private Stage stage;
    private String currentUserName;
    private String currentUserRole;
    private DiscussionBoardDAO dao;

    //UI components
    private ListView<Question> questionListView;
    private TextArea questionDetailArea;
    private ListView<Answer> answerListView;
    private ListView<Reply> replyListView;
    private ListView<Review> reviewListView;
    private TextField searchField;
    private ComboBox<String> filterComboBox;

    //currently selected question 
    private Question selectedQuestion;
    
    //currently selected answer
    private Answer selectedAnswer;

    public DiscussionBoardPage(Stage stage, String currentUserName, String currentUserRole) {
        this.stage = stage;
        this.currentUserName = currentUserName;
        this.currentUserRole = currentUserRole;

        try {
            this.dao = new DiscussionBoardDAO();
        } catch (SQLException e) {
            showError("Failed to connect to the database");
        }
    }

    //create the scene for UI
    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        //top: search and filter
        mainLayout.setTop(createTopSection());

        //left: question list and review list
        mainLayout.setLeft(createQuestionsSection());

        //center: question detail, answer list, and reply list
        mainLayout.setCenter(createDetailSection());

        //right: action buttons.
        mainLayout.setRight(createActionSection());

        return new Scene(mainLayout, 1200, 800);
    }

    //create the top layout for search and filter
    private VBox createTopSection() {
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));
        Label titleLabel = new Label("Discussion Board");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Search questions...");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> performSearch());

        Button clearSearchButton = new Button("Clear");
        clearSearchButton.setOnAction(e -> clearSearch());

        searchBox.getChildren().addAll(new Label("Search:"), searchField, searchButton, clearSearchButton);

        //filter 
        HBox filterBox = new HBox(10);
        filterComboBox = new ComboBox<>();
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Answered", "Unanswered", "My Questions"));
        filterComboBox.setValue("All");
        filterComboBox.setOnAction(e -> applyFilter());

        filterBox.getChildren().addAll(new Label("Filter by:"), filterComboBox);
        topBox.getChildren().addAll(titleLabel, searchBox, filterBox);
        return topBox;
}
    //Create left side with question list and review list
    private VBox createQuestionsSection() {
        VBox questionsBox = new VBox(10);
        questionsBox.setPadding(new Insets(10));
        questionsBox.setPrefWidth(350);

        Label questionLabel = new Label("Questions");
        questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        questionListView = new ListView<>();
        questionListView.setPrefHeight(400);

        //cell factory for question list    
        questionListView.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);
                if (empty || question == null) {
                    setText(null);
                } else{
                    String status = question.getIsAnswered() ? "[✓]" : "[?]";
                    setText(status + " " + question.getTitle()+ " (" + question.getAuthorUserName() + ")");
    
                }
            }
        });

        questionListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> displayQuestionDetail(newVal)); 

        loadQuestions();

        //review list
        Label reviewLabel = new Label("Reviews");
        reviewLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        reviewListView = new ListView<>();
        reviewListView.setPrefHeight(200);

        //cell factory for review list
        reviewListView.setCellFactory(lv -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                } else {
                    setText(review.getRating() + "/5 stars: " + review.getContent() + "\n - " + review.getAuthorUserName() + " (" + review.getCreatedAt().toLocalDate() + ")");
                }
            }
        });
        
        questionsBox.getChildren().addAll(questionLabel, questionListView, reviewLabel, reviewListView);
        return questionsBox;
}
    //create center section with question detail, answer list, and reply list
    private VBox createDetailSection() {
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));

        //question detail
        Label detailLabel = new Label("Question Details");
        detailLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        questionDetailArea = new TextArea();
        questionDetailArea.setEditable(false);
        questionDetailArea.setPrefHeight(200);
        questionDetailArea.setWrapText(true);

        //answer list
        Label answerLabel = new Label("Answers");
        answerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        answerListView = new ListView<>();
        answerListView.setPrefHeight(175);

        //cell factory for answer list
        answerListView.setCellFactory(lv -> new ListCell<Answer>() {
            @Override
            protected void updateItem(Answer answer, boolean empty) {
                super.updateItem(answer, empty);
                if (empty || answer == null) {
                    setText(null);
                } else {
                    String status = answer.getIsAccepted() ? "[ACCEPTED] " : "";
                    setText(status + answer.getContent() + "\n - " + answer.getAuthorUserName() + " (" + answer.getCreatedAt().toLocalDate() + ")");
                }
            }
        });
        answerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> displayAnswerDetail(newVal));
        
        //reply list
        Label replyLabel = new Label("Replies");
        replyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        replyListView = new ListView<>();
        replyListView.setPrefHeight(175);

        //cell factory for reply list
        replyListView.setCellFactory(lv -> new ListCell<Reply>() {
            @Override
            protected void updateItem(Reply reply, boolean empty) {
                super.updateItem(reply, empty);
                if (empty || reply == null) {
                    setText(null);
                } else {
                    setText(reply.getContent() + "\n - " + reply.getAuthorUserName() + " (" + reply.getCreatedAt().toLocalDate() + ")");
                }
            }
        });

        detailBox.getChildren().addAll(detailLabel, questionDetailArea, answerLabel, answerListView, replyLabel, replyListView);
        return detailBox;
}
//create right section with action buttons
    private VBox createActionSection() {
        VBox actionBox = new VBox(10);
        actionBox.setPadding(new Insets(10));
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPrefWidth(200);

        //add question button
        Button createQuestionBtn = new Button("Create Question");
        createQuestionBtn.setPrefWidth(180);
        createQuestionBtn.setOnAction(e -> createQuestion());
        //edit question
        Button editQuestionBtn = new Button("Edit Question");
        editQuestionBtn.setPrefWidth(180);
        editQuestionBtn.setOnAction(e -> editQuestion());
        //delete question
        Button deleteQuestionBtn = new Button("Delete Question");
        deleteQuestionBtn.setPrefWidth(180);
        deleteQuestionBtn.setOnAction(e -> deleteQuestion());
        //add answer
        Button addAnswerBtn = new Button("Add Answer");
        addAnswerBtn.setPrefWidth(180);
        addAnswerBtn.setOnAction(e -> addAnswer());
        //edit answer
        Button editAnswerBtn = new Button("Edit Answer");
        editAnswerBtn.setPrefWidth(180);
        editAnswerBtn.setOnAction(e -> editAnswer());
        //delete answer
        Button deleteAnswerBtn = new Button("Delete Answer");
        deleteAnswerBtn.setPrefWidth(180);
        deleteAnswerBtn.setOnAction(e -> deleteAnswer());
        //add reply
        Button addReplyBtn = new Button("Add Reply");
        addReplyBtn.setPrefWidth(180);
        addReplyBtn.setOnAction(e -> addReply());
        //edit reply
        Button editReplyBtn = new Button("Edit Reply");
        editReplyBtn.setPrefWidth(180);
        editReplyBtn.setOnAction(e -> editReply());
        //delete reply
        Button deleteReplyBtn = new Button("Delete Reply");
        deleteReplyBtn.setPrefWidth(180);
        deleteReplyBtn.setOnAction(e -> deleteReply());
        //add review
        Button addReviewBtn = new Button("Add Review");
        addReviewBtn.setPrefWidth(180);
        addReviewBtn.setOnAction(e -> addReview());
        //edit review
        Button editReviewBtn = new Button("Edit Review");
        editReviewBtn.setPrefWidth(180);
        editReviewBtn.setOnAction(e -> editReview());
        //delete review
        Button deleteReviewBtn = new Button("Delete Review");
        deleteReviewBtn.setPrefWidth(180);
        deleteReviewBtn.setOnAction(e -> deleteReview());
        //refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefWidth(180);
        refreshBtn.setOnAction(e -> refreshData());
        //back button
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(180);
        backBtn.setOnAction(e -> goBack());
        actionBox.getChildren().addAll(
            createQuestionBtn, editQuestionBtn, deleteQuestionBtn,
            new Separator(),
            addAnswerBtn, editAnswerBtn, deleteAnswerBtn,
            new Separator(),
            addReplyBtn, editReplyBtn, deleteReplyBtn,
            new Separator(),
            addReviewBtn, editReviewBtn, deleteReviewBtn,
            new Separator(),
            refreshBtn, backBtn
        );
        return actionBox;
    }

    //crud operations.

      //create a question
      private void createQuestion() {
        //dialog for creating a question
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Question");
        dialog.setHeaderText("Enter the details of the question");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField titleField = new TextField();
        titleField.setPromptText("Enter the title of the question");
        TextArea contentField = new TextArea();
        contentField.setPromptText("Enter the content of the question");
        contentField.setPrefRowCount(5);
        contentField.setWrapText(true);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Enter the category of the question (optional)");
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
    
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String title = titleField.getText();
                String content = contentField.getText();
                String category = categoryField.getText();
                //validate the question
                String error = DiscussionBoardValidator.validateQuestion(title, content, category);
                if (error != null) {
                    showError(error);
                    return;
                }
                //create the question
                Question newQuestion = new Question(title.trim(), content.trim(), currentUserName);
                if (category != null && !category.trim().isEmpty()) {
                    newQuestion.setCategory(category.trim());
                }
                
                try {
                    dao.createQuestion(newQuestion);
                    showInfo("Question created successfully!");
                    refreshData();
                } catch (SQLException e) {
                    showError("Failed to create question: " + e.getMessage());
                }
            }
        });
    }

    //update a question
    private void editQuestion() {
        //for selecting and validating
        if (selectedQuestion == null) {
            showError("Please select a question to edit");
            return;
        }
        //check permissions (only admin or author can edit)
        if(!selectedQuestion.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to edit this question");
            return;
        }
        //dialog for editing a question
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Modify the question details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        TextField titleField = new TextField(selectedQuestion.getTitle());
        TextArea contentField = new TextArea(selectedQuestion.getContent());
        contentField.setPrefRowCount(5);
        contentField.setWrapText(true);
        TextField categoryField = new TextField(selectedQuestion.getCategory() != null ? selectedQuestion.getCategory() : "");
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String title = titleField.getText();
                String content = contentField.getText();
                String category = categoryField.getText();
                //validate the question
                String error = DiscussionBoardValidator.validateQuestion(title, content, category);
                if (error != null) {
                    showError(error);
                    return;
                }
                //update the question
                selectedQuestion.setTitle(title.trim());
                selectedQuestion.setContent(content.trim());
                if (category != null && !category.trim().isEmpty()) {
                    selectedQuestion.setCategory(category.trim());
                }
                try {
                    dao.updateQuestion(selectedQuestion);
                    showInfo("Question updated successfully!");
                    refreshData();
                } catch (SQLException e) {
                    showError("Failed to update question: " + e.getMessage());
                }
            }
        });
}

    //delete a question
    private void deleteQuestion() {
        if (selectedQuestion == null) {
            showError("Please select a question to delete");
            return;
        }
        //check permissions (only admin or author can delete)
        if(!selectedQuestion.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to delete this question");
            return;
        }
        //dialog for deleting a question
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Question");
        confirm.setHeaderText("Are you sure you want to delete this question?");
        confirm.setContentText("This action cannot be undone, this will delete all answers associated with this question.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.deleteQuestion(selectedQuestion.getQuestionId());
                    showInfo("Question deleted successfully");
                    selectedQuestion = null;
                    refreshData();
                } catch (SQLException e) {
                    showError("Failed to delete question: " + e.getMessage());
                }
            }
        });
    }

    //add an answer
    private void addAnswer() {
        if (selectedQuestion == null) {
            showError("Please select a question to add an answer");
            return;
        }
        //dialog for adding an answer
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Answer");
        dialog.setHeaderText("Add answer to: " + selectedQuestion.getTitle());
        dialog.setContentText("Enter the content of the answer");

        dialog.showAndWait().ifPresent(response -> {
            String error = DiscussionBoardValidator.validateAnswer(response);
            if (error != null) {
                showError(error);
                return;
            }
            Answer newAnswer = new Answer(selectedQuestion.getQuestionId(), response.trim(), currentUserName);
            try {
                dao.createAnswer(newAnswer);
                selectedQuestion.setIsAnswered(true);
                dao.updateQuestion(selectedQuestion);
                showInfo("Answer added successfully!");
                displayQuestionDetail(selectedQuestion);
            } catch (SQLException e) {
                showError("Failed to add answer: " + e.getMessage());
            }
        });
    }
    //edit an answer
    private void editAnswer() {
        if (selectedAnswer == null) {
            showError("Please select an answer to edit");
            return;
        }
        //check permissions (only author or admin can edit)
        if(!selectedAnswer.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to edit this answer");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selectedAnswer.getContent());
        dialog.setTitle("Edit Answer");
        dialog.setContentText("Answer:");

        dialog.showAndWait().ifPresent(content -> {
            String error = DiscussionBoardValidator.validateAnswer(content);
            if (error != null) {
                showError(error);
                return;
            }
            selectedAnswer.setContent(content.trim());
            try {
                dao.updateAnswer(selectedAnswer);
                showInfo("Answer updated successfully!");
                displayQuestionDetail(selectedQuestion);
            } catch (SQLException e) {
                showError("Failed to update answer: " + e.getMessage());
            }
        });
    }
    //delete an answer
    private void deleteAnswer() {
        if (selectedAnswer == null) {
        showError("Please select an answer to delete");
            return;
        }
        //check permissions (only author or admin can delete)
        if(!selectedAnswer.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to delete this answer");
            return;
        }
        //dialog for deleting an answer
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Answer");
        confirm.setHeaderText("Are you sure you want to delete this answer?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.deleteAnswer(selectedAnswer.getAnswerId());
                    showInfo("Answer deleted successfully");
                    displayQuestionDetail(selectedQuestion);
                } catch (SQLException e) {
                    showError("Failed to delete answer: " + e.getMessage());
                }
            }
        });
    }
    //add a reply
    private void addReply() {
        if (selectedAnswer == null) {
            showError("Please select an answer to add a reply");
            return;
        }
        //dialog for adding a reply
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Answer");
        dialog.setHeaderText("Add reply to: " + selectedAnswer.getContent());
        dialog.setContentText("Enter the content of the reply");

        dialog.showAndWait().ifPresent(response -> {
            String error = DiscussionBoardValidator.validateReply(response);
            if (error != null) {
                showError(error);
                return;
            }
            Reply newReply = new Reply(selectedAnswer.getAnswerId(), response.trim(), currentUserName);
            try {
                dao.createReply(newReply);
                showInfo("Reply added successfully!");
                displayAnswerDetail(selectedAnswer);
            } catch (SQLException e) {
                showError("Failed to add reply: " + e.getMessage());
            }
        });
    }
    //edit a reply
    private void editReply() {
        Reply selectedReply = replyListView.getSelectionModel().getSelectedItem();
        if (selectedReply == null) {
            showError("Please select a reply to edit");
            return;
        }
        //check permissions (only author or admin can edit)
        if(!selectedReply.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to edit this reply");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selectedReply.getContent());
        dialog.setTitle("Edit Reply");
        dialog.setContentText("Reply:");

        dialog.showAndWait().ifPresent(content -> {
            String error = DiscussionBoardValidator.validateReply(content);
            if (error != null) {
                showError(error);
                return;
            }
            selectedReply.setContent(content.trim());
            try {
                dao.updateReply(selectedReply);
                showInfo("Reply updated successfully!");
                displayAnswerDetail(selectedAnswer);
            } catch (SQLException e) {
                showError("Failed to update reply: " + e.getMessage());
            }
        });
    }
    //delete a reply
    private void deleteReply() {
        Reply selectedReply = replyListView.getSelectionModel().getSelectedItem();
        if (selectedReply == null) {
        showError("Please select a reply to delete");
            return;
        }
        //check permissions (only author or admin can delete)
        if(!selectedReply.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to delete this reply");
            return;
        }
        //dialog for deleting a reply
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Reply");
        confirm.setHeaderText("Are you sure you want to delete this reply?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.deleteReply(selectedReply.getReplyId());
                    showInfo("Reply deleted successfully");
                    displayAnswerDetail(selectedAnswer);
                } catch (SQLException e) {
                    showError("Failed to delete reply: " + e.getMessage());
                }
            }
        });
    }
    //add a review
    private void addReview() {
    	if (selectedQuestion == null && selectedAnswer == null) {
            showError("Please select a question or an answer to add a review");
            return;
        }
    	//dialog for creating a review
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Review");
        dialog.setHeaderText("Enter the details of the review");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField ratingField = new TextField();
        ratingField.setPromptText("Enter the rating between 1-5 stars");
        TextArea contentField = new TextArea();
        contentField.setPromptText("Enter the content of the review");
        contentField.setPrefRowCount(5);
        contentField.setWrapText(true);
        
        grid.add(new Label("Rating:"), 0, 0);
        grid.add(ratingField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentField, 1, 1);
    
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String rating = ratingField.getText();
                String content = contentField.getText();
                //validate the review
                String error = DiscussionBoardValidator.validateReview(rating, content);
                if (error != null) {
                    showError(error);
                    return;
                }
                //create the review
                if (selectedAnswer != null) {
                	Review newReview = new Review(Integer.parseInt(rating.trim()), -1, selectedAnswer.getAnswerId(), content.trim(), currentUserName);
                	try {
                        dao.createReview(newReview);
                        showInfo("Review created successfully!");
                        displayAnswerDetail(selectedAnswer);
                    } catch (SQLException e) {
                        showError("Failed to create review: " + e.getMessage());
                    }
                }
                else {
                	Review newReview = new Review(Integer.parseInt(rating.trim()), selectedQuestion.getQuestionId(), -1, content.trim(), currentUserName);
                	try {
                        dao.createReview(newReview);
                        showInfo("Review created successfully!");
                        displayQuestionDetail(selectedQuestion);
                    } catch (SQLException e) {
                        showError("Failed to create review: " + e.getMessage());
                    }
                }
            }
        });
    }
    //edit a review
    private void editReview() {
    	Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
    	if (selectedReview == null) {
    		showError("Please select a review to edit");
    		return;
    	}
    	//check permissions (only author or admin can edit)
        if(!selectedReview.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to edit this review");
            return;
        }
        //dialog for editing a question
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Review");
        dialog.setHeaderText("Modify the review details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        TextField ratingField = new TextField(Integer.toString(selectedReview.getRating()));
        TextArea contentField = new TextArea(selectedQuestion.getContent());
        contentField.setPrefRowCount(5);
        contentField.setWrapText(true);
        
        grid.add(new Label("Rating:"), 0, 0);
        grid.add(ratingField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String rating = ratingField.getText();
                String content = contentField.getText();
                //validate the review
                String error = DiscussionBoardValidator.validateReview(rating, content);
                if (error != null) {
                    showError(error);
                    return;
                }
                //update the review
                selectedReview.setRating(Integer.parseInt(rating.trim()));
                selectedReview.setContent(content.trim());
                try {
                    dao.updateReview(selectedReview);
                    showInfo("Review updated successfully!");
                    if (selectedAnswer != null) {
                    	displayAnswerDetail(selectedAnswer);
                    }
                    else {
                    	displayQuestionDetail(selectedQuestion);
                    }
                } catch (SQLException e) {
                    showError("Failed to update review: " + e.getMessage());
                }
            }
        });
    }
    //delete a review
    private void deleteReview() {
    	Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
    	if (selectedReview == null) {
    		showError("Please select a review to delete");
    		return;
    	}
    	//check permissions (only author or admin can delete)
        if(!selectedReview.getAuthorUserName().equals(currentUserName) && !currentUserRole.equals("admin")) {
            showError("You are not authorized to delete this review");
            return;
        }
        //dialog for deleting a review
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Review");
        confirm.setHeaderText("Are you sure you want to delete this review?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.deleteReview(selectedReview.getReviewId());
                    showInfo("Review deleted successfully");
                    if (selectedAnswer != null) {
                    	displayAnswerDetail(selectedAnswer);
                    }
                    else {
                    	displayQuestionDetail(selectedQuestion);
                    }
                } catch (SQLException e) {
                    showError("Failed to delete review: " + e.getMessage());
                }
            }
        });
    }

    //helper methods

    //load questions 
    private void loadQuestions() {
        try {
            Questions questions = dao.getAllQuestions();
            ObservableList<Question> questionList = FXCollections.observableArrayList(questions.getAllQuestions());
            questionListView.setItems(questionList);
        } catch (SQLException e) { showError("Failed to load questions: " + e.getMessage());}
    }
        //display question detail
        private void displayQuestionDetail(Question question) {
            selectedQuestion = question;
            if(question == null){
                questionDetailArea.clear();
                answerListView.setItems(FXCollections.observableArrayList());
                reviewListView.setItems(FXCollections.observableArrayList());
                return;
            }
            String details = "Title: " + question.getTitle() + "\n\n" +
            "Author: " + question.getAuthorUserName() + "\n" +
            "Category: " + (question.getCategory() != null ? question.getCategory() : "N/A") + "\n" +
            "Created At: " + question.getCreatedAt().toLocalDate() + "\n" +
            "Status: " + (question.getIsAnswered() ? "Answered" : "Unanswered") + "\n\n" +
            "Content:\n" + question.getContent();
            questionDetailArea.setText(details);

            //load answers
            try {
                Answers answers = dao.getAnswersForQuestion(question.getQuestionId());
                ObservableList<Answer> answerList = FXCollections.observableArrayList(answers.getAllAnswers());
                answerListView.setItems(answerList);
            } catch (SQLException e) { showError("Failed to load answers: " + e.getMessage());}
            //load reviews
            try {
                Reviews reviews = dao.getReviewsForQuestion(question.getQuestionId());
                ObservableList<Review> reviewList = FXCollections.observableArrayList(reviews.getAllReviews());
                reviewListView.setItems(reviewList);
            } catch (SQLException e) { showError("Failed to load reviews: " + e.getMessage());}
        }
        //display answer's replies and reviews
        private void displayAnswerDetail(Answer answer) {
        	selectedAnswer = answer;
        	if(answer == null) {
        		replyListView.setItems(FXCollections.observableArrayList());
        		reviewListView.setItems(FXCollections.observableArrayList());
        		return;
        	}
        	try {
        		Replies replies = dao.getRepliesForAnswer(answer.getAnswerId());
        		ObservableList<Reply> replyList = FXCollections.observableArrayList(replies.getAllReplies());
        		replyListView.setItems(replyList);
        	} catch (SQLException e) { showError("Failed to load replies: " + e.getMessage());}
        	//load reviews
            try {
                Reviews reviews = dao.getReviewsForAnswer(answer.getAnswerId());
                ObservableList<Review> reviewList = FXCollections.observableArrayList(reviews.getAllReviews());
                reviewListView.setItems(reviewList);
            } catch (SQLException e) { showError("Failed to load reviews: " + e.getMessage());}
        }
        //perform search
        private void performSearch() {
            String keyword = searchField.getText();
            String error = DiscussionBoardValidator.validateSearchQuery(keyword);
            if (error != null) {
                showError(error);
                return;
            }
            try {
                Questions allQuestions = dao.getAllQuestions();
                Questions searchResults = allQuestions.search(keyword);
                ObservableList<Question> resultList = FXCollections.observableArrayList(searchResults.getAllQuestions());
                questionListView.setItems(resultList);
            } catch (SQLException e) { showError("Failed to search questions: " + e.getMessage());}
        }
        //clear search
        private void clearSearch() {
            searchField.clear();
            filterComboBox.setValue("All");
            loadQuestions();
        }

        //filter questions
        private void applyFilter() {
            try {
                Questions allQuestions = dao.getAllQuestions();
                Questions filtered;
                String filter = filterComboBox.getValue();

                switch (filter) {
                    case "Answered":
                        filtered = allQuestions.filterByAnsweredStatus(true);
                        break;
                    case "Unanswered":
                        filtered = allQuestions.filterByAnsweredStatus(false);
                        break;
                    case "My Questions":
                        filtered = allQuestions.filterByAuthor(currentUserName);
                        break;
                    default:
                        filtered = allQuestions;
                        break;
                }
                ObservableList<Question> resultList = FXCollections.observableArrayList(filtered.getAllQuestions());
                questionListView.setItems(resultList);
            } catch (SQLException e) { showError("Failed to filter questions: " + e.getMessage());}
        }
        //refresh data
        private void refreshData() {
            loadQuestions();
            if(selectedQuestion != null) {
                try {
                    Question refreshed = dao.getQuestionById(selectedQuestion.getQuestionId());
                    displayQuestionDetail(refreshed);
                }catch (SQLException e) {displayQuestionDetail(null);}
            }
            if(selectedAnswer != null) {
                try {
                    Answer refreshed = dao.getAnswerById(selectedAnswer.getAnswerId());
                    displayAnswerDetail(refreshed);
                }catch (SQLException e) {displayAnswerDetail(null);}
            }
        }

    //navigate to home page for role
    private void goBack() {
        if(currentUserRole.equals("admin")) {
            AdminHomePage adminHomePage = new AdminHomePage(stage,currentUserName);
            stage.setScene(adminHomePage.createScene());
        } else {
            UserHomePage userHomePage = new UserHomePage(stage,currentUserName);
            stage.setScene(userHomePage.createScene());
        }
    }
    //Show error and info messages
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
