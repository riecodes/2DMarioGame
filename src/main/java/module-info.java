module com.mycompany.dmariogame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.dmariogame to javafx.fxml;
    exports com.mycompany.dmariogame;
}
