package gui;


import java.awt.AWTException;
import java.awt.Robot;

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


public class GUI extends Application
{
	static VBox console;	
	
	@Override public void start(Stage primaryStage) throws InterruptedException
	{
		try
		{
			Logic.pc = new Robot();
		}
		catch (AWTException e1)
		{
			// Cannot communicate with the PC. Exiting.
			ConfirmationBox warning = new ConfirmationBox(primaryStage.getOwner(), ""
					+ "FishingBot has failed to communicate with your client controls.\n"
					+ "The program must now exit.");
			warning.showAndWait();
			System.exit(1);
			e1.printStackTrace();
		}
		
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
			if (Logic.calibrationPoint != null)
			{
				// User wants to start fishing.
				Logic.fishingActive = true;
				sendAlert("Fishing Mode: ON");
				
				// Loop should be on it's own thread so it doesn't freeze the GUI.
				new Thread(new Runnable() 
			    { 
					public void run() 
					{ 
						// Continue until told otherwise.
						while (Logic.fishingActive)
						{
							// Start fishing.
							Logic.sleep(3000);
							Logic.Say("/cast Fishing");
							
							// Look the the bobber.
							Logic.sleep(500);
							if (Logic.scanForBobber())
							{
								Logic.attemptToReel();
							}
						}
					} 
			    }).start(); 
			}
			else 
			{
				ConfirmationBox warning = new ConfirmationBox(primaryStage.getOwner(), ""
						+ "FishingBot must be calibrated first before it can be used!");
				warning.showAndWait();
			}
		});
		
		// End Fishing Button.
		Button end = new Button("End Fishing");
		end.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		end.setOnAction(e ->		
		{
			if (Logic.fishingActive)
			{
				// User wants to stop fishing.
				Logic.fishingActive = false;
				sendAlert("Fishing Mode: OFF");
			}
		});
		
		// Calibration Button.
		Button calibrate = new Button("Calibrate");
		calibrate.setTooltip(new Tooltip("Before each use, press this button and hover over the\n"
						+ "fishing bobber for five seconds, or until you see \"CALBIRATED\""));
		calibrate.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		calibrate.setOnAction(e ->
		{
			sendAlert(Logic.calibrate() ? "Calibration successful." : "Calibration FAILED.");
		});

		// Add all the fields to the layout.
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
	}
}
