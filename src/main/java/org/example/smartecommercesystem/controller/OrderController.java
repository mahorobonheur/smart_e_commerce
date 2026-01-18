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
import org.example.smartecommercesystem.dao.OrderDAO;
import org.example.smartecommercesystem.model.Order;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class OrderController {

    private HelloApplication app;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, Integer> userIdColumn;
    @FXML private TableColumn<Order, Double> totalColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> dateColumn;
    @FXML private TableColumn<Order, Void> updateColumn;
    @FXML private TableColumn<Order, Void> deleteColumn;

    @FXML private ComboBox<String> sortBox;

    private ObservableList<Order> masterList;
    private SortedList<Order> sortedList;

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        setupTable();
        setupSorting();
        loadOrders();
        addUpdateButton();
        addDeleteButton();
    }

    public void goBack(ActionEvent e) throws IOException {
        app.showDashBoard();
    }


    private void setupTable() {
        orderIdColumn.setCellValueFactory(d ->
                new ReadOnlyObjectWrapper<>(d.getValue().getOrderId()));

        userIdColumn.setCellValueFactory(d ->
                new ReadOnlyObjectWrapper<>(d.getValue().getUserId()));

        totalColumn.setCellValueFactory(d ->
                new ReadOnlyObjectWrapper<>(d.getValue().getTotal()));

        statusColumn.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getStatus()));

        dateColumn.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getOrderDate() != null
                                ? d.getValue().getOrderDate().toLocalDateTime().toString()
                                : ""
                ));
    }


    private void loadOrders() {
        List<Order> orders = new OrderDAO().getAllOrders();
        masterList = FXCollections.observableArrayList(orders);

        sortedList = new SortedList<>(masterList);
        ordersTable.setItems(sortedList);

        applySorting();
    }


    private void setupSorting() {
        sortBox.getItems().addAll(
                "Order ID (Ascending)",
                "Order ID (Descending)",
                "Date (Newest)",
                "Date (Oldest)",
                "Total (High → Low)",
                "Total (Low → High)",
                "Status (A → Z)",
                "Status (Z → A)"
        );

        sortBox.getSelectionModel().selectFirst();
        sortBox.setOnAction(e -> applySorting());
    }

    private void applySorting() {
        if (sortedList == null) return;

        Comparator<Order> comparator = null;

        switch (sortBox.getValue()) {

            case "Order ID (Ascending)" ->
                    comparator = Comparator.comparingInt(Order::getOrderId);

            case "Order ID (Descending)" ->
                    comparator = Comparator.comparingInt(Order::getOrderId).reversed();

            case "Date (Newest)" ->
                    comparator = Comparator.comparing(
                            Order::getOrderDate,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ).reversed();

            case "Date (Oldest)" ->
                    comparator = Comparator.comparing(
                            Order::getOrderDate,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    );

            case "Total (High → Low)" ->
                    comparator = Comparator.comparingDouble(Order::getTotal).reversed();

            case "Total (Low → High)" ->
                    comparator = Comparator.comparingDouble(Order::getTotal);

            case "Status (A → Z)" ->
                    comparator = Comparator.comparing(
                            Order::getStatus,
                            String.CASE_INSENSITIVE_ORDER
                    );

            case "Status (Z → A)" ->
                    comparator = Comparator.comparing(
                            Order::getStatus,
                            String.CASE_INSENSITIVE_ORDER
                    ).reversed();
        }

        sortedList.setComparator(comparator);
    }


    private void addUpdateButton() {
        updateColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Update Status");

            {
                btn.setStyle(
                        "-fx-background-color: #3498db;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 6;" +
                                "-fx-font-weight: bold;" +
                                "-fx-cursor: hand;"
                );

                btn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());

                    ChoiceDialog<String> dialog = new ChoiceDialog<>(
                            order.getStatus(),
                            "Pending", "Shipped", "Delivered", "Cancelled"
                    );

                    dialog.setTitle("Update Status");
                    dialog.setHeaderText("Order #" + order.getOrderId());
                    dialog.setContentText("Select new status:");

                    dialog.showAndWait().ifPresent(newStatus -> {
                        if (new OrderDAO().updateOrderStatus(order.getOrderId(), newStatus)) {
                            order.setStatus(newStatus);
                            ordersTable.refresh();
                            showAlert(Alert.AlertType.INFORMATION, "Order status updated!");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Failed to update status!");
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


    private void addDeleteButton() {
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");

            {
                btn.setStyle(
                        "-fx-background-color: #e74c3c;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 6;" +
                                "-fx-font-weight: bold;" +
                                "-fx-cursor: hand;"
                );

                btn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Delete Order #" + order.getOrderId() + "?",
                            ButtonType.OK, ButtonType.CANCEL
                    );

                    confirm.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.OK) {
                            if (new OrderDAO().deleteOrder(order.getOrderId())) {
                                masterList.remove(order);
                                showAlert(Alert.AlertType.INFORMATION, "Order deleted!");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Failed to delete order!");
                            }
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

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
