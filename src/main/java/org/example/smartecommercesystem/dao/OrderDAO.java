package org.example.smartecommercesystem.dao;

import org.example.smartecommercesystem.model.Order;
import org.example.smartecommercesystem.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final String URL = System.getenv("DB_URL");
    private final String USERNAME = System.getenv("DB_USERNAME");
    private final String PASSWORD = System.getenv("DB_PASSWORD");

    public Order addOrder(Order order) {
        String orderSql = "INSERT INTO orders(user_id, total, status, order_date) VALUES (?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            connection.setAutoCommit(false); // start transaction

            try (
                    PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement itemStmt = connection.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS)
            ) {

                orderStmt.setInt(1, order.getUserId());
                orderStmt.setDouble(2, order.getTotal());
                orderStmt.setString(3, order.getStatus());
                orderStmt.setTimestamp(4, order.getOrderDate() != null ? order.getOrderDate() : new Timestamp(System.currentTimeMillis()));

                int affectedRows = orderStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating order failed, no rows affected.");
                }

                try (ResultSet keys = orderStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setOrderId(keys.getInt(1));
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }


                for (OrderItem item : order.getItems()) {
                    itemStmt.setInt(1, order.getOrderId());
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.executeUpdate();

                    try (ResultSet itemKeys = itemStmt.getGeneratedKeys()) {
                        if (itemKeys.next()) {
                            item.setItemId(itemKeys.getInt(1));
                            item.setOrderId(order.getOrderId());
                        }
                    }
                }

                connection.commit();
                return order;

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Order getOrder(int orderId) {
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String itemsSql = "SELECT * FROM order_items WHERE order_id = ?";
        Order order = null;

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement orderStmt = connection.prepareStatement(orderSql);
             PreparedStatement itemsStmt = connection.prepareStatement(itemsSql)) {

            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();
            if (orderRs.next()) {
                order = new Order();
                order.setOrderId(orderRs.getInt("order_id"));
                order.setUserId(orderRs.getInt("user_id"));
                order.setTotal(orderRs.getDouble("total"));
                order.setStatus(orderRs.getString("status"));
                order.setOrderDate(orderRs.getTimestamp("order_date"));


                itemsStmt.setInt(1, orderId);
                ResultSet itemsRs = itemsStmt.executeQuery();
                List<OrderItem> items = new ArrayList<>();
                while (itemsRs.next()) {
                    OrderItem item = new OrderItem();
                    item.setItemId(itemsRs.getInt("item_id"));
                    item.setOrderId(itemsRs.getInt("order_id"));
                    item.setProductId(itemsRs.getInt("product_id"));
                    item.setQuantity(itemsRs.getInt("quantity"));
                    item.setPrice(itemsRs.getDouble("price"));
                    items.add(item);
                }
                order.setItems(items);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }


    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String orderSql = "SELECT * FROM orders";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(orderSql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Order order = getOrder(orderId);
                if (order != null) orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }


    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean deleteOrder(int orderId) {
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            connection.setAutoCommit(false);

            try (PreparedStatement itemsStmt = connection.prepareStatement(deleteItemsSql);
                 PreparedStatement orderStmt = connection.prepareStatement(deleteOrderSql)) {

                itemsStmt.setInt(1, orderId);
                itemsStmt.executeUpdate();

                orderStmt.setInt(1, orderId);
                int affected = orderStmt.executeUpdate();

                connection.commit();
                return affected > 0;

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
