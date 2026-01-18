package org.example.smartecommercesystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.UserDAO;
import org.example.smartecommercesystem.model.User;

import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField fullNames;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private SplitMenuButton role;
    @FXML
    private Label message;

    private HelloApplication app;

    @FXML
    private void onRoleSelected(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        role.setText(item.getText());
    }

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    public void submit(ActionEvent e){
        if(fullNames.getText().isEmpty()){
            message.setText("Full Names Can not be empty!");
        } else if (!validateName(fullNames.getText())){
            message.setText("Please enter a valid name. (Eg. John Paul, O'Connor, etc...");
        } else if(email.getText().isEmpty()){
            message.setText("Email must be provided");
        } else if(!validateEmail(email.getText())){
            message.setText("Email format is not acceptable");
        } else if(validatePassword(password.getText())){
            message.setText("Password must be at least 8 characters, 1 digit, 1 special character");
        }else if(validatePassword(confirmPassword.getText())){
            message.setText("Password must be at least 8 characters, 1 digit, 1 special character");
        } else if(role.getText().isEmpty()){
            message.setText("Role can not be empty!");
        } else if(!password.getText().equals(confirmPassword.getText())){
            message.setText("Passwords do not match");
        } else {
            User user = new User(email.getText(), password.getText(), fullNames.getText(), role.getText());
            UserDAO userDAO = new UserDAO();
            userDAO.createUser(user);
            message.setText("User created successfully!");

            try{
                app.showLoginPage();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void loginButton(ActionEvent e) throws IOException {
        app.showLoginPage();
    }

    public void resetPasswordButton(ActionEvent e) throws IOException{
        app.showForgotPasswordpage();
    }


    public boolean validateEmail(String email){
        if(email.length() < 6){
            message.setText("Email characters are too few");
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean validatePassword(String password){
        return !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}");
    }

    public boolean validateName(String name){
        return name.matches("^[A-Za-z]+([ '-][A-Za-z]+)");
    }
}
