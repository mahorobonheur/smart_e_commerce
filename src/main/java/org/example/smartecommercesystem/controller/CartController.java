package org.example.smartecommercesystem.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.OrderDAO;
import org.example.smartecommercesystem.model.*;
import org.example.smartecommercesystem.session.Session;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CartController {

    private HelloApplication app;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> productColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, Double> priceColumn;

    @FXML
    private TableColumn<CartItem, Double> totalColumn;

    @FXML
    private Label totalLabel;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    public void setCartItems(ObservableList<CartItem> items) {
        this.cartItems = items;
        cartTable.setItems(cartItems);
        updateTotal();
    }

    public ObservableList<CartItem> getCartItems() {
        return cartItems;
    }

    @FXML
    public void initialize() {
        productColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getProduct().getProductName()));
        quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
        priceColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getProduct().getPrice()));
        totalColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTotal()));

        cartTable.setItems(cartItems);
        updateTotal();
    }

    // Add product to cart
    public void addToCart(int userId, Product product, int quantity) {
        cartItems.stream()
                .filter(item -> item.getProduct().getProductId() == product.getProductId())
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> cartItems.add(new CartItem(product, quantity)));
        cartTable.refresh();
        updateTotal();
    }

    // Remove selected item
    public void removeFromCart(ActionEvent e) {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
            updateTotal();
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select an item to remove.");
        }
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    public void placeOrder(ActionEvent e) {

        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cart is empty.");
            return;
        }

        User user = Session.getInstance().getCurrentUser();

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "You must be logged in to place an order.");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProduct().getProductId());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());
            orderItems.add(oi);
        }

        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setTotal(
                orderItems.stream()
                        .mapToDouble(oi -> oi.getPrice() * oi.getQuantity())
                        .sum()
        );
        order.setStatus("Pending");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setItems(orderItems);

        OrderDAO dao = new OrderDAO();
        Order result = dao.addOrder(order);

        if (result != null) {
            cartItems.clear();
            updateTotal();
            showAlert(Alert.AlertType.INFORMATION,
                    "Order placed successfully!\nOrder ID: " + result.getOrderId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to place order!");
        }
    }


    public void goBack(ActionEvent e) throws IOException {
        app.showDashBoard();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
