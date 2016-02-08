package gui;


import java.awt.AWTException;
import java.awt.Robot;
import java.time.LocalTime;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;


public class GUI extends Application
{
	private VBox root;
	private Scene scene;
	private Stage stage;
	private Label consoleTitle;
	private static TextArea console;
	private Button start, end, calibrate;
	
	private final int SCENE_WIDTH = 500, SCENE_HEIGHT = 500;
	
	@Override public void start(Stage primaryStage) throws InterruptedException
	{
		try
		{
			// Initialize the Robot from the Logic class.
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
		
		Stopwatch watch = new Stopwatch();
		
		// Root Pane.
		root = new VBox();
		root.setPadding(new Insets(10, 10, 0, 10));
		root.setAlignment(Pos.TOP_CENTER);
		root.setSpacing(10);
		
		// Title for Console.
		consoleTitle = new Label("Console");
		consoleTitle.setFont(Font.font ("Verdana", 20));
		consoleTitle.setTextFill(Color.web("#0076a3"));
		
		
		// Create the console.
		console = new TextArea();
		console.setEditable(false);
		console.setMaxHeight(200);
		console.setMinHeight(200);
		console.setMinWidth(SCENE_WIDTH - 20);
		console.setMaxWidth(SCENE_WIDTH - 20);
		
		
		// Start Fishing Button.
		start = new Button("Start Fishing");
		start.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		start.setOnAction(e ->		
		{
			// Fishing must be calibrated before use.
			if (Logic.calibrationPoint != null)
			{
				// Fishing cannot be activated twice.
				if (!Logic.fishingActive)
				{
					// User wants to start fishing.
					sendAlert("Fishing Mode: ON");
					Logic.fishingActive = true;
					
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
									sendAlert(Logic.attemptToReel() ? "Fish caught and looted." 
											: "Was unable to detect splash!");
								}
								else 
								{
									sendAlert("Could not find bobber!");
								}
							}
						} 
				    }).start(); 
				}
			}
			else 
			{
				ConfirmationBox warning = new ConfirmationBox(primaryStage.getOwner(), ""
						+ "FishingBot must be calibrated first before it can be used!");
				warning.showAndWait();
			}
		});
		
		
		// End Fishing Button.
		end = new Button("End Fishing");
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
		calibrate = new Button("Calibrate");
		calibrate.setTooltip(new Tooltip("Before each use, press this button and hover over the\n"
						+ "fishing bobber for five seconds, or until you see \"CALBIRATED\""));
		calibrate.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		calibrate.setOnAction(e ->		
		{
			// Loop should be on it's own thread so it doesn't freeze the GUI.
			new Thread(new Runnable() 
		    { 				
				public void run() 
				{
					sendAlert("Calibrating in 4 seconds...");
					// Print whether the Calibration was successful.
					sendAlert(Logic.calibrate() ? "Calibration successful." : "Calibration FAILED.");
				} 
		    }).start(); 
		});
		

		// Add all the fields to the layout.
		root.getChildren().addAll(consoleTitle, console, start, end, calibrate, watch);
		
		stage = primaryStage;
		scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		stage.setTitle("FishingBot");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
	
	/**
	 * Print out a message to the console.
	 * @param message to be displayed.
	 */
	public static void sendAlert(String message)
	{		
		LocalTime now = LocalTime.now();
		
		String hour = "", minute = "", second = "";
		hour = (now.getHour() > 12) ? "" + (now.getHour() % 12) : "" + now.getHour();
		minute = (now.getMinute() < 10) ? "0" + now.getMinute() : "" + now.getMinute();
		second = (now.getSecond() < 10) ? "0" + now.getSecond() : "" + now.getSecond();

		console.setText(
				hour + ":" + minute + ":" + second + 
				((now.getHour() < 12) ? " AM" : " PM") + " : " + message
				+ "\n" + console.getText());
	}
}
