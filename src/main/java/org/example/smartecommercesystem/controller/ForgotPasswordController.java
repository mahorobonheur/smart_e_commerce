package org.example.smartecommercesystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.UserDAO;

import java.io.IOException;

public class ForgotPasswordController {
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label message;

    private HelloApplication app;

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    public void resetPassword(ActionEvent e) throws IOException {
        if(email.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()){
            message.setText("Please insert into all fields!");
        } else if(validatePassword(confirmPassword.getText()) || validatePassword(password.getText())){
            message.setText("Password must be at least 8 characters, 1 digit, 1 special character");
        } else if(!password.getText().equals(confirmPassword.getText())){
            message.setText("Passwords do not match!");
        }else{
            UserDAO userDAO = new UserDAO();
            boolean updated = userDAO.updateUser(email.getText(), password.getText());
            if(!updated){
                message.setText("Can not update user!");
            } else {
                message.setText("User updated successfully");
                app.showDashBoard();
            }
        }


    }

    public boolean validatePassword(String password){
        return !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}");
    }

    public void signUp(ActionEvent e ) throws IOException{
        app.showSignUpPage();
    }

    public void login(ActionEvent e) throws IOException{
        app.showLoginPage();
    }
}
