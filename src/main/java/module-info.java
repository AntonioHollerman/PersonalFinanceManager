module gui.personalfinancemanager {
    requires java.sql;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires eu.hansolo.tilesfx;

    opens gui.personalfinancemanager to javafx.fxml;
    exports gui.personalfinancemanager;
}