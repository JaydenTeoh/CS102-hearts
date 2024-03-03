module com.example._102_test_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example._102_test_project to javafx.fxml;
    exports com.example._102_test_project;
}