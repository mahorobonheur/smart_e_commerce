package org.example.smartecommercesystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.smartecommercesystem.controller.*;

import java.io.IOException;

public class HelloApplication extends Application {

    CartController cartController = new CartController();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;


        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);


        FXMLLoader cartLoader = new FXMLLoader(getClass().getResource("cart.fxml"));
        Parent cartRoot = cartLoader.load();
        cartController = cartLoader.getController();

        showSignUpPage();

        primaryStage.centerOnScreen();
        primaryStage.show();
    }


    private void switchScene(Parent root, String title) {
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.centerOnScreen();

        primaryStage.sizeToScene();
        primaryStage.setMaximized(false);
        primaryStage.setFullScreen(false);

        primaryStage.hide();
        primaryStage.show();
    }

    public void showLoginPage() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setApp(this);
        switchScene(root, "Login Page");
    }

    public void showSignUpPage() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
        Parent root = loader.load();
        SignUpController signUpController = loader.getController();
        signUpController.setApp(this);
        switchScene(root, "SignUp Page");
    }

    public void showForgotPasswordpage() throws IOException{
        FXMLLoader loader = new FXMLLoader((getClass().getResource("forgot_password.fxml")));
        Parent root = loader.load();
        ForgotPasswordController forgotPasswordController = loader.getController();
        forgotPasswordController.setApp(this);
        switchScene(root, "Reset Password Page");
    }

    public void showDashBoard() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        Parent root = loader.load();
        DashboardController dashboardController = loader.getController();
        dashboardController.setApp(this);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard");
       primaryStage.setMaximized(true);
    }

    public void showProductsPage() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("product.fxml"));
        Parent root= loader.load();
        Scene scene = new Scene(root);
        ProductController productController = loader.getController();
        productController.setCartController(cartController);
        productController.setApp(this);
        primaryStage.setTitle("Products");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }

    public void showCategoriesPage() throws IOException{
        FXMLLoader  loader = new FXMLLoader(getClass().getResource("categories.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        CategoryController categoryController = loader.getController();
        categoryController.setApp(this);
        primaryStage.setTitle("Categories");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }

    public void showCartPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cart.fxml"));
        Parent root = loader.load();
        CartController controller = loader.getController();
        controller.setApp(this);
        controller.setCartItems(cartController.getCartItems());
        switchScene(root, "Cart");
    }

    public void showOrders() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("orders.fxml"));
        Parent root = loader.load();
        OrderController controller = loader.getController();
        controller.setApp(this);
        switchScene(root, "Orders");
    }

    public void showReviews() throws IOException{
        FXMLLoader loader = new FXMLLoader((getClass().getResource("review.fxml")));
        Parent root = loader.load();
        ReviewController controller = loader.getController();
        controller.setApp(this);
        switchScene(root, "Reviews");
    }

    public static void main(String[] args) {
        launch(args);
    }
}