package org.example.smartecommercesystem.dao;

import org.example.smartecommercesystem.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final String URL = System.getenv("DB_URL");
    private final String USERNAME = System.getenv("DB_USERNAME");
    private final String PASSWORD = System.getenv("DB_PASSWORD");

    public Category addCategory(Category category){
        try{

            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("insert into categories(name) values(?)");
            statement.setString(1, category.getCategoryName());

            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0){
                return category;
            }

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public Category getCategory(int id){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("Select* from categories where category_id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Category category = new Category();
                category.setCategoryId(resultSet.getInt("category_id"));
                category.setCategoryName(resultSet.getString("name"));
                return category;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public List<Category> getAllCategories(){
        List<Category> categories = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select* from categories");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Category category = new Category();
               category.setCategoryId(resultSet.getInt("category_id"));
               category.setCategoryName(resultSet.getString("name"));
               categories.add(category);
            }
            return categories;

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return categories;
    }

    public boolean updateCategory(Category category){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("update categories set name = ? where category_id = ?");
            statement.setString(1, category.getCategoryName());
            statement.setInt(2, category.getCategoryId());
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0){
                return true;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteCategory(int id){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("delete from categories where category_id = ?");
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
