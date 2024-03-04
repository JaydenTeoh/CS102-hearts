module com.example.hearts102 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.hearts102 to javafx.fxml;
    exports com.example.hearts102;
}