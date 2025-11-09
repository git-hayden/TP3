module FoundationCode {
	requires javafx.controls;
	requires java.sql;

	opens application to javafx.graphics, javafx.fxml;
	opens databasePart1;
	exports application;
	exports databasePart1;
}
