package application;
	
import java.io.File;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;


public class Main extends Application {
	public Stage primaryStage;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Rodger's Little Scrapper");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(confirmCloseEventHandler);
			
	
	        Button closeButton = new Button("Close Application");
	        closeButton.setOnAction(event ->
	        primaryStage.fireEvent(
	                        new WindowEvent(
	                        		primaryStage,
	                                WindowEvent.WINDOW_CLOSE_REQUEST
	                        )
	                )
	        );

	       StackPane layout = new StackPane(closeButton);
	       layout.setPadding(new Insets(10));
	            
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/*
	 * For Window-close button alert
	 */
	  private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
	        Alert closeConfirmation = new Alert(
	                Alert.AlertType.CONFIRMATION,
	                "Are you sure you want to exit?"
	        );
	        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(
	                ButtonType.OK
	        );
	        exitButton.setText("Exit");
	        closeConfirmation.setHeaderText("Confirm Exit");
	        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
	        closeConfirmation.initOwner(primaryStage);

	        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
	        if (!ButtonType.OK.equals(closeResponse.get())) {
	            event.consume();
	        }
	    };
}
