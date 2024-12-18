module gui.personalfinancemanager {
    requires java.sql;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires eu.hansolo.tilesfx;

    opens gui to javafx.fxml;
    opens gui.controller to javafx.fxml;

    exports gui;
    exports gui.controller;
    exports structure;
}