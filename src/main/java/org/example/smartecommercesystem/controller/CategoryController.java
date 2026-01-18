package org.example.smartecommercesystem.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.CategoryDAO;
import org.example.smartecommercesystem.model.Category;
import org.example.smartecommercesystem.session.Session;

import java.io.IOException;
import java.util.List;

public class CategoryController {

    private HelloApplication app;
    private Category editingCategory;

    @FXML private TextField categoryName;
    @FXML private ComboBox<String> sortBox;

    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, Integer> idColumn;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private TableColumn<Category, Void> editColumn;
    @FXML private TableColumn<Category, Void> deleteColumn;

    private ObservableList<Category> masterList;
    private SortedList<Category> sortedList;

    @FXML
    public void initialize() {

        if (!Session.getInstance().isLoggedIn()) {
            showAlert(Alert.AlertType.ERROR, "Please login first.");
            return;
        }

        if (!Session.getInstance().isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Admin access only.");
            disableAdminFeatures();

        }

        idColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getCategoryId())
        );

        nameColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getCategoryName())
        );

        setupSorting();
        addEditButton();
        addDeleteButton();
        loadAllCategories();
    }

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    public void goBack(ActionEvent e) throws IOException {
        app.showDashBoard();
    }


    public void addCategory(ActionEvent e) {

        if (!Session.getInstance().isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Admin access only.");
            return;
        }

        String name = categoryName.getText().trim();

        if (name.length() < 2) {
            showAlert(Alert.AlertType.ERROR, "Name must be at least 2 characters.");
            return;
        }

        CategoryDAO dao = new CategoryDAO();

        if (editingCategory == null) {
            Category c = new Category();
            c.setCategoryName(name);
            dao.addCategory(c);
        } else {
            editingCategory.setCategoryName(name);
            dao.updateCategory(editingCategory);
            editingCategory = null;
        }

        categoryName.clear();
        loadAllCategories();
    }



    private void loadAllCategories() {
        List<Category> list = new CategoryDAO().getAllCategories();
        masterList = FXCollections.observableArrayList(list);

        sortedList = new SortedList<>(masterList);
        categoriesTable.setItems(sortedList);

        applySorting();
    }

    private void setupSorting() {

        sortBox.getItems().addAll(
                "ID (Ascending)",
                "ID (Descending)",
                "Name (A → Z)",
                "Name (Z → A)"
        );

        sortBox.getSelectionModel().selectFirst();
        sortBox.setOnAction(e -> applySorting());
    }

    private void applySorting() {

        String choice = sortBox.getValue();
        if (choice == null) return;

        switch (choice) {
            case "ID (Ascending)" ->
                    sortedList.setComparator(
                            (a, b) -> Integer.compare(
                                    a.getCategoryId(),
                                    b.getCategoryId()
                            )
                    );

            case "ID (Descending)" ->
                    sortedList.setComparator(
                            (a, b) -> Integer.compare(
                                    b.getCategoryId(),
                                    a.getCategoryId()
                            )
                    );

            case "Name (A → Z)" ->
                    sortedList.setComparator(
                            (a, b) -> a.getCategoryName()
                                    .compareToIgnoreCase(
                                            b.getCategoryName()
                                    )
                    );

            case "Name (Z → A)" ->
                    sortedList.setComparator(
                            (a, b) -> b.getCategoryName()
                                    .compareToIgnoreCase(
                                            a.getCategoryName()
                                    )
                    );
        }
    }


    private void addEditButton() {
        editColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Edit");

            {
                btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold;");
                btn.setOnAction(e -> {
                    editingCategory = getTableView().getItems().get(getIndex());
                    categoryName.setText(editingCategory.getCategoryName());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void addDeleteButton() {
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");

            {
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold;");
                btn.setOnAction(e -> {
                    Category c = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Delete " + c.getCategoryName() + "?",
                            ButtonType.OK, ButtonType.CANCEL
                    );

                    confirm.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.OK) {
                            new CategoryDAO().deleteCategory(c.getCategoryId());
                            loadAllCategories();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void disableAdminFeatures() {
        categoryName.setDisable(true);
        editColumn.setVisible(false);
        deleteColumn.setVisible(false);
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
