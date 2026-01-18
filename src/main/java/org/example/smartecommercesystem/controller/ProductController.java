package org.example.smartecommercesystem.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.bson.Document;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.CategoryDAO;
import org.example.smartecommercesystem.dao.ProductDAO;
import org.example.smartecommercesystem.dao.ReviewNoSQLDAO;
import org.example.smartecommercesystem.model.Category;
import org.example.smartecommercesystem.model.Product;
import org.example.smartecommercesystem.model.Review;
import org.example.smartecommercesystem.model.User;
import org.example.smartecommercesystem.session.Session;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ProductController {

    private HelloApplication app;
    private Product editingProduct;
    private CartController cartController;

    private ObservableList<Category> categoriesList;
    private ObservableList<Product> productList;
    private ObservableList<Review> reviewList;

    private final ReviewNoSQLDAO reviewDAO = new ReviewNoSQLDAO();

    @FXML private TextField productName, price, stock;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private ComboBox<String> sortByCombo, sortOrderCombo;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, String> dateColumn;
    @FXML private TableColumn<Product, Void> editColumn, deleteColumn;

    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, String> reviewIdColumn;
    @FXML private TableColumn<Review, Integer> userIdColumn;
    @FXML private TableColumn<Review, Integer> ratingColumn;
    @FXML private TableColumn<Review, String> commentColumn;
    @FXML private TableColumn<Review, String> reviewDateColumn;
    @FXML private TableColumn<Review, Void> reviewDeleteColumn;

    @FXML private ComboBox<Integer> ratingCombo;
    @FXML private TextField commentField;

    @FXML
    public void initialize() {
        if (!Session.getInstance().isLoggedIn()) {
            showAlert(Alert.AlertType.ERROR, "Please login first.");
            return;
        }

        setupProductTable();
        setupReviewTable();
        loadCategories();
        loadProducts();


        if (Session.getInstance().isAdmin()) {
            addEditButton();
            addDeleteButton();
        } else {
            editColumn.setVisible(false);
            deleteColumn.setVisible(false);
        }

        // Sorting options
        sortByCombo.setItems(FXCollections.observableArrayList("Name","Date","Price"));
        sortOrderCombo.setItems(FXCollections.observableArrayList("Ascending","Descending"));
        sortByCombo.setValue("Name"); sortOrderCombo.setValue("Ascending");

        ratingCombo.getItems().addAll(1,2,3,4,5);

        // Load reviews when product selected
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP != null) loadReviews(newP.getProductId());
        });
    }

    public void setApp(HelloApplication app) { this.app = app; }
    public void setCartController(CartController cartController) { this.cartController = cartController; }
    public void goBack(ActionEvent e) throws IOException { app.showDashBoard(); }

    private void setupProductTable() {
        idColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getProductId()));
        nameColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getProductName()));
        priceColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPrice()));
        priceColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : "Rwf " + String.format("%.2f", price));
            }
        });
        stockColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getStock()));
        categoryColumn.setCellValueFactory(d -> {
            int catId = d.getValue().getCategoryId();
            String name = categoriesList.stream()
                    .filter(c -> c.getCategoryId() == catId)
                    .map(Category::getCategoryName)
                    .findFirst().orElse("Unknown");
            return new ReadOnlyStringWrapper(name);
        });
        dateColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getCreatedAt() == null ? "" : d.getValue().getCreatedAt().toLocalDateTime().toString()
        ));
    }

    private void loadCategories() {
        categoriesList = FXCollections.observableArrayList(new CategoryDAO().getAllCategories());
        categoryComboBox.setItems(categoriesList);
    }

    private void loadProducts() {
        productList = FXCollections.observableArrayList(new ProductDAO().getAllProducts());
        productTable.setItems(productList);
    }

    public void addProduct(ActionEvent e) {
        if (!Session.getInstance().isAdmin()) { showAlert(Alert.AlertType.ERROR,"Admin access only!"); return; }
        if (productName.getText().isEmpty() || price.getText().isEmpty() || stock.getText().isEmpty() || categoryComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "All fields are required"); return;
        }

        try {
            ProductDAO dao = new ProductDAO();
            if (editingProduct == null) {
                Product p = new Product();
                p.setProductName(productName.getText());
                p.setPrice(Double.parseDouble(price.getText()));
                p.setStock(Integer.parseInt(stock.getText()));
                p.setCategoryId(categoryComboBox.getValue().getCategoryId());
                dao.addProduct(p);
                showAlert(Alert.AlertType.INFORMATION, "Product added successfully!");

            } else {
                editingProduct.setProductName(productName.getText());
                editingProduct.setPrice(Double.parseDouble(price.getText()));
                editingProduct.setStock(Integer.parseInt(stock.getText()));
                editingProduct.setCategoryId(categoryComboBox.getValue().getCategoryId());
                dao.updateProduct(editingProduct);
                showAlert(Alert.AlertType.INFORMATION, "Product updated successfully!");
                editingProduct = null;
            }
            clearForm(); loadProducts();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid price or stock");
        }
    }

    public void sortProducts(ActionEvent e) {
        String sortBy = sortByCombo.getValue(), order = sortOrderCombo.getValue();
        if (sortBy == null || order == null) return;
        Comparator<Product> cmp = switch (sortBy) {
            case "Name" -> Comparator.comparing(Product::getProductName);
            case "Date" -> Comparator.comparing(Product::getCreatedAt);
            case "Price" -> Comparator.comparingDouble(Product::getPrice);
            default -> null;
        };
        if (cmp != null && "Descending".equals(order)) cmp = cmp.reversed();
        if (cmp != null) productList.sort(cmp);
    }

    private void addEditButton() {
        editColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Edit");
            { btn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;-fx-background-radius:6;");
                btn.setOnAction(e -> {
                    editingProduct = getTableView().getItems().get(getIndex());
                    productName.setText(editingProduct.getProductName());
                    price.setText(String.valueOf(editingProduct.getPrice()));
                    stock.setText(String.valueOf(editingProduct.getStock()));
                    categoryComboBox.setValue(
                            categoriesList.stream().filter(c -> c.getCategoryId() == editingProduct.getCategoryId()).findFirst().orElse(null)
                    );
                });
            }
            @Override protected void updateItem(Void i, boolean empty){ super.updateItem(i, empty); setGraphic(empty ? null : btn); }
        });
    }

    private void addDeleteButton() {
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            { btn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:6;");
                btn.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + p.getProductName() + "?", ButtonType.OK, ButtonType.CANCEL);
                    confirm.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.OK) {
                            new ProductDAO().deleteProduct(p.getProductId());
                            loadProducts();
                        }
                    });
                });
            }
            @Override protected void updateItem(Void i, boolean empty){ super.updateItem(i, empty); setGraphic(empty ? null : btn); }
        });
    }

    public void addToCart(ActionEvent e) {

        User user = Session.getInstance().getCurrentUser();
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Please login first.");
            return;
        }

        Product product = productTable.getSelectionModel().getSelectedItem();
        if (product == null) {
            showAlert(Alert.AlertType.WARNING, "Select a product first.");
            return;
        }

        TextInputDialog d = new TextInputDialog("1");
        d.setHeaderText("Quantity for " + product.getProductName());

        d.showAndWait().ifPresent(q -> {
            try {
                int qty = Integer.parseInt(q);
                if (qty < 1) throw new NumberFormatException();
                cartController.addToCart(user.getUserId(), product, qty);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid quantity");
            }
        });
    }


    private void clearForm() { productName.clear(); price.clear(); stock.clear(); categoryComboBox.setValue(null); }

    private void showAlert(Alert.AlertType type, String msg){ Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }


    private void setupReviewTable() {
        reviewIdColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getReviewId()));
        userIdColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getUserId()));
        ratingColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getRating()));
        commentColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getComment()));
        reviewDateColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCreatedAt().toLocalDateTime().toString()));
        addReviewDeleteButton();
    }

    private void loadReviews(int productId) {
        reviewList = FXCollections.observableArrayList();
        List<Document> docs = reviewDAO.getAllReviews();
        for (Document doc : docs) {
            if (doc.getInteger("productId") == productId) {
                Review r = new Review();
                r.setReviewId(doc.getObjectId("_id").toHexString());
                r.setProductId(doc.getInteger("productId"));
                r.setUserId(doc.getInteger("userId"));
                r.setRating(doc.getInteger("rating"));
                r.setComment(doc.getString("comment"));
                r.setCreatedAt(new java.sql.Timestamp(doc.getLong("createdAt")));
                reviewList.add(r);
            }
        }
        reviewTable.setItems(reviewList);
    }

    @FXML
    public void addReview(ActionEvent e){
        if(!Session.getInstance().isLoggedIn()){ showAlert(Alert.AlertType.ERROR,"Login required!"); return;}
        Product p = productTable.getSelectionModel().getSelectedItem();
        if(p == null){ showAlert(Alert.AlertType.WARNING,"Select product first"); return;}
        Integer rating = ratingCombo.getValue(); String comment = commentField.getText();
        if(rating == null || comment.isEmpty()){ showAlert(Alert.AlertType.ERROR,"Rating and comment required"); return;}
        reviewDAO.addReview(null, p.getProductId(), Session.getInstance().getCurrentUser().getUserId(), rating, comment);
        loadReviews(p.getProductId());
        commentField.clear(); ratingCombo.setValue(null);
        showAlert(Alert.AlertType.INFORMATION,"Review added!");
    }

    private void addReviewDeleteButton() {
        reviewDeleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            { btn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:6;");
                btn.setOnAction(e -> {
                    Review r = getTableView().getItems().get(getIndex());
                    if(r.getUserId() != Session.getInstance().getCurrentUser().getUserId()){ showAlert(Alert.AlertType.ERROR,"You can only delete your own reviews."); return;}
                    reviewDAO.deleteReview(r.getReviewId());
                    loadReviews(r.getProductId());
                });
            }
            @Override protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }
}
