package org.example.smartecommercesystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.CategoryDAO;
import org.example.smartecommercesystem.dao.OrderDAO;
import org.example.smartecommercesystem.dao.ProductDAO;
import org.example.smartecommercesystem.dao.UserDAO; // Assuming this exists
import org.example.smartecommercesystem.model.Category;
import org.example.smartecommercesystem.model.Order;
import org.example.smartecommercesystem.model.Product;
import org.example.smartecommercesystem.model.User;
import org.example.smartecommercesystem.session.Session;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    private HelloApplication app;

    @FXML private Label welcomeLabel;
    @FXML private Label productsCountLabel;
    @FXML private Label ordersCountLabel;
    @FXML private Label revenueLabel;
    @FXML private Label customersCountLabel;
    @FXML private Label avgOrderValueLabel;
    @FXML private Label lowStockProductsLabel;

    @FXML private TextField searchField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;

    private FilteredList<Product> filteredProducts;
    private ObservableList<Product> masterProducts;
    private Map<Integer, String> categoryMap;

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        loadUser();
        setupProductTable();
        loadStats();
    }

    private void loadUser() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFullName());
        } else {
            welcomeLabel.setText("Welcome, Guest");
        }
    }

    private void setupProductTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void loadStats() {
        ProductDAO productDAO = new ProductDAO();
        OrderDAO orderDAO = new OrderDAO();
        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        List<Product> products = productDAO.getAllProducts();
        List<Order> orders = orderDAO.getAllOrders();
        List<User> users = userDAO.getAllUsers();
        List<Category> categories = categoryDAO.getAllCategories();

        // Create category map for quick lookup
        categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getCategoryName));

        productsCountLabel.setText(String.valueOf(products.size()));
        ordersCountLabel.setText(String.valueOf(orders.size()));

        double revenue = orders.stream().mapToDouble(Order::getTotal).sum();
        revenueLabel.setText(String.format("%.2f", revenue));

        int customersCount = users.size();
        customersCountLabel.setText(String.valueOf(customersCount));

        double avgOrderValue = orders.isEmpty() ? 0 : revenue / orders.size();
        avgOrderValueLabel.setText(String.format("%.2f", avgOrderValue));

        long lowStockProducts = products.stream().filter(p -> p.getStock() < 10).count();
        lowStockProductsLabel.setText(String.valueOf(lowStockProducts));

        masterProducts = FXCollections.observableArrayList(products);
        filteredProducts = new FilteredList<>(masterProducts, p -> true);
        productTable.setItems(filteredProducts);

        // Add global search listener (partial match on product name or category name)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProducts.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                String categoryName = categoryMap.getOrDefault(product.getCategoryId(), "");
                return product.getProductName().toLowerCase().contains(lowerCaseFilter) ||
                        categoryName.toLowerCase().contains(lowerCaseFilter);
            });
        });

        productTable.refresh();
    }

    public void logout(ActionEvent event) throws IOException {
        Session.getInstance().logout();
        app.showLoginPage();
    }

    public void showProducts(ActionEvent event) throws IOException {
        if (checkLoggedIn()) app.showProductsPage();
    }

    public void showCategories(ActionEvent event) throws IOException {
        if (checkLoggedIn()) app.showCategoriesPage();
    }

    public void showCart(ActionEvent event) throws IOException {
        if (checkLoggedIn()) app.showCartPage();
    }

    public void showOrders(ActionEvent event) throws IOException {
        if (checkLoggedIn()) app.showOrders();
    }

    public void showReviews(ActionEvent event) throws IOException{
        if(checkLoggedIn()) app.showReviews();
    }

    private boolean checkLoggedIn() {
        if (!Session.getInstance().isLoggedIn()) {
            showAlert("Not Logged In", "Please login first.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}