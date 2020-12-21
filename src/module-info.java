module Workshop {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens gui ;
    opens application;
    opens model.services;
    opens model.entities;

}