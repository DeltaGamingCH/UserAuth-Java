module zngr {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.codec;
    requires java.sql;

    opens zngr to javafx.fxml;
    exports zngr;
}
