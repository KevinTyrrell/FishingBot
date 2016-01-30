package gui;


import program.*;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;


@SuppressWarnings("restriction") public class GUI extends Application
{
	Logic app = new Logic();
	static VBox console;	
	
	@Override public void start(Stage primaryStage) throws InterruptedException
	{
		VBox root = new VBox();
		root.setPadding(new Insets(10, 10, 0, 10));
		root.setAlignment(Pos.TOP_CENTER);
		root.setSpacing(10);
		
		Scene scene = new Scene(root, 500, 500);

		Label consoleTitle = new Label("Console");
		consoleTitle.setFont(Font.font ("Verdana", 20));
		consoleTitle.setTextFill(Color.web("#0076a3"));
		
		console = new VBox();
		ScrollPane scrollingConsole = new ScrollPane();
		scrollingConsole.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollingConsole.setHbarPolicy(ScrollBarPolicy.NEVER);								
		scrollingConsole.setMaxHeight(224);													
		scrollingConsole.setMinHeight(224);													
		scrollingConsole.setMaxWidth(536);
		scrollingConsole.setContent(console);
		
		Button start = new Button("Start Fishing");
		start.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		start.setOnAction(e ->		
		{
			if (app.getCalibrationPoint() != null)
			{
				// User wants to start fishing.
				app.setFishingActive(true);
				sendAlert("Fishing Mode: ON");
				
				// Must be created in its own thread.
				new Thread(new Runnable() 
			    { 
					public void run() 
					{ 
						// Continue until told otherwise.
						while (app.isFishingActive())
						{
							Logic.sleep(3000);
							
							app.Say("/cast Fishing");
							
							Logic.sleep(500);
							
							if (app.scanForBobber())
							{
								app.attemptToReel();
							}
						}
					} 
			    }).start(); 
			}
			else 
			{
				sendAlert("Error! Cannot run while uncalibrated!");
			}
		});
		
		Button end = new Button("End Fishing");
		end.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		end.setOnAction(e ->		
		{
			if (app.isFishingActive())
			{
				// User wants to start fishing.
				app.setFishingActive(false);
				sendAlert("Fishing Mode: OFF");
			}
		});
		
		Button calibrate = new Button("Calibrate");
		calibrate.setTooltip(new Tooltip("Before each use, press this button and hover over the\n"
						+ "fishing bobber for five seconds, or until you see \"CALBIRATED\""));
		calibrate.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		calibrate.setOnAction(e ->
		{
			sendAlert("Calibrating");
			app.calibrate();
		});

		root.getChildren().addAll(consoleTitle, scrollingConsole, start, end, calibrate);

		primaryStage.setTitle("FishingBot");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
	
	/**
	 * Prints out text to the GUI textbox.
	 * @param message
	 */
	public static void sendAlert(String message)
	{		
		console.getChildren().add(0, new Label(message));
		
		/*LinkedList<Node> notifications = new LinkedList<>(console.getChildren());
		notifications.addFirst(new Label(message));
		console.getChildren().clear();
		
		for (Node i : notifications)
		{
			console.getChildren().add(i);
		}*/
	}
}
