package org.example.smartecommercesystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.smartecommercesystem.HelloApplication;
import org.example.smartecommercesystem.dao.UserDAO;
import org.example.smartecommercesystem.model.User;
import org.example.smartecommercesystem.session.Session;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Label message;

    private HelloApplication app;

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    public void login(ActionEvent e) throws IOException {
        if(email.getText().isEmpty() || password.getText().isEmpty()){
            message.setText("Please insert into all fields!");
        }else {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.selectUser(email.getText(), password.getText());
            if(user == null){
                message.setText("User with email: " + email.getText() + " is not found!");
            } else {
                Session.getInstance().login(user);
                message.setText("Login Successful!");
                app.showDashBoard();
            }


        }

    }

    public void signUp(ActionEvent e ) throws IOException{
        app.showSignUpPage();
    }

    public void forgotPassword(ActionEvent e) throws IOException{
        app.showForgotPasswordpage();
    }
}
