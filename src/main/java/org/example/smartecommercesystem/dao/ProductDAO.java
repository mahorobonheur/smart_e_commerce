package org.example.smartecommercesystem.dao;

import org.example.smartecommercesystem.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final String URL = System.getenv("DB_URL");
    private final String USERNAME = System.getenv("DB_USERNAME");
    private final String PASSWORD = System.getenv("DB_PASSWORD");

    public Product addProduct(Product product){
        try{

            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("insert into products(name, price, stock, category_id) values(?,?,?,?)");
            statement.setString(1, product.getProductName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getStock());
            statement.setInt(4, product.getCategoryId());
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0){
                return product;
            }

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public Product getProduct(int id){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("Select* from products where product_id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Product product = new Product();
                product.setProductId(resultSet.getInt("product_id"));
                product.setProductName(resultSet.getString("name"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));
                product.setCreatedAt(resultSet.getTimestamp("created_at"));
                return product;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
            return null;
    }

    public List<Product> getAllProducts(){
        List<Product> products = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select* from products");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Product product = new Product();
                product.setProductId(resultSet.getInt("product_id"));
                product.setProductName(resultSet.getString("name"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));
                product.setCategoryId(resultSet.getInt("category_id"));
                product.setCreatedAt(resultSet.getTimestamp("created_at"));
                products.add(product);
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return products;
    }

    public boolean updateProduct(Product product){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("update products set name = ?, price = ?, stock = ?, category_id = ? where product_id = ?");
            statement.setString(1, product.getProductName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getStock());
            statement.setInt(4, product.getCategoryId());
            statement.setInt(5, product.getProductId());

            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0){
                return true;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteProduct(int id){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("delete from products where product_id = ?");
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0){
                return true;
            }

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }
}


