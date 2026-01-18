package org.example.smartecommercesystem.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.ReviewNoSQLDAO;
import org.example.smartecommercesystem.model.Review;
import org.example.smartecommercesystem.session.Session;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class ReviewController {

    private HelloApplication app;

    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, String> reviewIdColumn;
    @FXML private TableColumn<Review, Integer> userIdColumn;
    @FXML private TableColumn<Review, Integer> ratingColumn;
    @FXML private TableColumn<Review, String> commentColumn;
    @FXML private TableColumn<Review, String> dateColumn;
    @FXML private TableColumn<Review, Void> actionColumn;

    private final ObservableList<Review> reviewList = FXCollections.observableArrayList();
    private final ReviewNoSQLDAO reviewDAO = new ReviewNoSQLDAO();

    public void setApp(HelloApplication app) { this.app = app; }

    @FXML
    public void initialize() {
        reviewIdColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getReviewId()));
        userIdColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getUserId()));
        ratingColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getRating()));
        commentColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getComment()));
        dateColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getCreatedAt().toString()));

        reviewTable.setItems(reviewList);

        setupActionButtons();
        loadReviews();
    }

    private void loadReviews() {
        reviewList.clear();
        List<Document> docs = reviewDAO.getAllReviews();
        for (Document doc : docs) {
            Review r = new Review();
            r.setReviewId(doc.getObjectId("_id").toHexString());
            r.setSqlId(doc.getInteger("sqlId"));
            r.setProductId(doc.getInteger("productId"));
            r.setUserId(doc.getInteger("userId"));
            r.setRating(doc.getInteger("rating"));
            r.setComment(doc.getString("comment"));
            r.setCreatedAt(new Timestamp(doc.getLong("createdAt")));
            reviewList.add(r);
        }
        reviewTable.refresh();
    }

    private void setupActionButtons() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");

                editBtn.setOnAction(e -> editReview());
                deleteBtn.setOnAction(e -> deleteReview());
            }

            private void editReview() {
                Review r = getTableView().getItems().get(getIndex());
                if (!Session.getInstance().isLoggedIn() || r.getUserId() != Session.getInstance().getCurrentUser().getUserId()) {
                    showAlert("Unauthorized", "You can only edit your own reviews."); return;
                }

                Dialog<Review> dialog = new Dialog<>();
                dialog.setTitle("Edit Review");

                Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, r.getRating());
                TextField commentField = new TextField(r.getComment());

                GridPane grid = new GridPane();
                grid.setHgap(10); grid.setVgap(10);
                grid.add(new Label("Rating:"), 0, 0); grid.add(ratingSpinner, 1, 0);
                grid.add(new Label("Comment:"), 0, 1); grid.add(commentField, 1, 1);
                dialog.getDialogPane().setContent(grid);

                ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

                dialog.setResultConverter(btn -> {
                    if (btn == saveButton) {
                        r.setRating(ratingSpinner.getValue());
                        r.setComment(commentField.getText());
                        return r;
                    }
                    return null;
                });

                dialog.showAndWait().ifPresent(updated -> {
                    reviewDAO.updateReview(updated.getReviewId(), updated.getRating(), updated.getComment());
                    loadReviews();
                    showAlert("Success", "Review updated!");
                });
            }

            private void deleteReview() {
                Review r = getTableView().getItems().get(getIndex());
                if (!Session.getInstance().isLoggedIn() || r.getUserId() != Session.getInstance().getCurrentUser().getUserId()) {
                    showAlert("Unauthorized", "You can only delete your own reviews."); return;
                }
                reviewDAO.deleteReview(r.getReviewId());
                loadReviews();
                showAlert("Deleted", "Review deleted successfully.");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    Review r = getTableView().getItems().get(getIndex());
                    if (Session.getInstance().isLoggedIn() && r.getUserId() == Session.getInstance().getCurrentUser().getUserId()) {
                        setGraphic(container);
                    } else setGraphic(null);
                }
            }
        });
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg);
        alert.showAndWait();
    }

    public void goBack(ActionEvent e) throws IOException {
        app.showProductsPage();
    }
}
