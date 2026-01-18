package org.example.smartecommercesystem.dao;

import org.example.smartecommercesystem.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final String URL = System.getenv("DB_URL");
    private final String USERNAME = System.getenv("DB_USERNAME");
    private final String PASSWORD = System.getenv("DB_PASSWORD");

    public User createUser(User user){
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement stat = connection.prepareStatement("Insert into users(email, password, full_name, role) values(?,?,?,?)");
            stat.setString(1, user.getEmail());
            stat.setString(2, user.getPassword());
            stat.setString(3, user.getFullName());
            stat.setString(4, user.getRole());
            stat.executeUpdate();
            stat.close();
            connection.close();
            return user;

        }catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }

    }

    public User selectUser(String email, String password){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement stat = connection.prepareStatement(
                    "SELECT user_id, full_name, password, role, created_at FROM users WHERE email = ?"
            );
            stat.setString(1, email);
            ResultSet resultSet = stat.executeQuery();

            if (resultSet.next()){
                String storedHashedPassword = resultSet.getString("password");
                if (BCrypt.checkpw(password, storedHashedPassword)){
                    User user = new User();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setFullName(resultSet.getString("full_name"));
                    user.setRole(resultSet.getString("role"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at"));
                    return user;
                }
            }

            return null;

        } catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
    }


    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select* from users");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                users.add(new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("full_name"),
                        resultSet.getString("role"),
                        resultSet.getTimestamp("created_at")));
            }

        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return users;
    }
    public boolean updateUser(String email, String password){
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
      try{
          Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
          PreparedStatement statement = connection.prepareStatement("update users set password = ? where email = ?");
          statement.setString(1, hashedPassword);
          statement.setString(2, email);
          int rowsAffected = statement.executeUpdate();
          return rowsAffected > 0;

      } catch (SQLException ex){
          ex.printStackTrace();
          return false;
      }
    }
}
