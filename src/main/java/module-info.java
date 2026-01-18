module org.example.smartecommercesystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires javafx.base;
    requires jbcrypt;
    requires org.mongodb.driver.sync.client;
    requires java.desktop;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    opens org.example.smartecommercesystem to javafx.fxml;
    opens org.example.smartecommercesystem.controller to javafx.fxml;
    opens org.example.smartecommercesystem.model to javafx.base, javafx.fxml;
    exports org.example.smartecommercesystem;
}