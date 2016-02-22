package gui;

import java.awt.AWTException;
import java.awt.Robot;
import java.time.LocalTime;

import program.*;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class GUI extends Application
{
	private VBox root;
	private Scene scene;
	private static Stage stage;
	private Label consoleTitle;
	private static TextArea console;
	private Button start, end, calibrate;

	private final int SCENE_WIDTH = 450, SCENE_HEIGHT = 400;
	public static final String DEBUG_PANE_BORDER = "-fx-border-color: blue;\n" + "-fx-border-insets: 5;\n" + "-fx-border-width: 3;\n" + "-fx-border-style: dashed;\n";

	@Override public void start(Stage stage) throws InterruptedException
	{
		// Assign the stage as an instance variable.
		GUI.stage = stage;

		
		SquareSelector square = new SquareSelector(stage.getOwner());
		System.exit(0);
		
		try 
		{
			// Initialize the Robot from the Logic class.
			Logic.pc = new Robot();
		}
		catch (AWTException e)
		{
			// Cannot communicate with the PC. Exiting.
			promptUser("FishingBot was unable to communicate with your computer. Stack trace is as follows: " + e.getStackTrace());
			System.exit(1);
		}

		// TESTING
		promptUser("FishingBot has detected the following display: " + Logic.display.getWidth() + " x " + Logic.display.getHeight() + ".");
		//Stopwatch watch = new Stopwatch();

		// Root Pane.
		root = new VBox();
		root.setPadding(new Insets(10, 10, 0, 10));
		root.setAlignment(Pos.TOP_CENTER);
		root.setSpacing(10);

		// Title for console.
		consoleTitle = new Label("Console");
		consoleTitle.setFont(Font.font("Verdana", 20));
		consoleTitle.setTextFill(Color.web("#0076a3"));

		// Create the console.
		console = new TextArea();
		console.setEditable(false);
		setRegionSize(console, SCENE_WIDTH - 20, 200);

		// Start Fishing Button.
		start = new Button("Run");
		start.setTooltip(new Tooltip("Begin the fishing loop, once calibrated."));
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
					consoleMessage("FISHING MODE: ON");
					Logic.fishingActive = true;

					// Loop should be on it's own thread so it doesn't freeze the GUI.
					new Thread(new Runnable()
					{
						public void run()
						{
							// Give the user time to click into WoW.
							Logic.sleep(3000);
							
							// Continue until told otherwise.
							while (Logic.fishingActive)
							{
								// Start fishing.
								Logic.Say("/cast Fishing");

								// Scan for the bobber, then reel the fish in. Print errors if something goes wrong.
								consoleMessage(Logic.scanForBobber() ? (Logic.attemptToReel() ? "Fish caught and looted."
										: "Located fish but failed to detect a splash!") : "Failed to detect the bobber!");
							}
						}
					}).start();
				}
			}
			else
			{
				// User tried to fish without calibrating.
				promptUser("You must first calibrate the program before you begin fishing!");
			}
		});

		// End Fishing Button.
		end = new Button("End");
		end.setTooltip(new Tooltip("Ends the fishing loop, if it is running."));
		end.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		end.setOnAction(e ->
		{
			if (Logic.fishingActive)
			{
				// User wants to stop fishing.
				Logic.fishingActive = false;
				consoleMessage("Fishing Mode: OFF");
			}
		});

		// Calibration Button.
		calibrate = new Button("Calibrate");
		calibrate.setTooltip(new Tooltip("Calibrate the program by manually casting your bobber, and hovering your mouse over it."));
		calibrate.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		calibrate.setOnAction(e ->
		{
			// Loop should be on it's own thread so it doesn't freeze the GUI.
			new Thread(new Runnable()
			{
				public void run()
				{
					// Print whether the Calibration was successful.
					consoleMessage("Calibration was " + (Logic.calibrate() ? "successful" : "unsuccessful") + ".");
				}
			}).start();
		});

		HBox buttonHB = new HBox(start, end, calibrate);
		buttonHB.setAlignment(Pos.CENTER);
		buttonHB.setSpacing(20);
		for (Node i : buttonHB.getChildren())
		{
			setRegionSize((Region) i, 125, 45);
		}

		// Add all the fields to the layout.
		root.getChildren().addAll(consoleTitle, console, buttonHB);

		scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		stage.setTitle("FishingBot");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Main method. Launch(args) searches for the method START in this class.
	 * @param args
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

	/**
	 * Print out a message to the console.
	 * @param message to be displayed.
	 */
	public static void consoleMessage(String message)
	{
		LocalTime now = LocalTime.now();

		String hour = "", minute = "", second = "";
		hour = (now.getHour() > 12) ? "" + (now.getHour() % 12) : "" + now.getHour();
		minute = (now.getMinute() < 10) ? "0" + now.getMinute() : "" + now.getMinute();
		second = (now.getSecond() < 10) ? "0" + now.getSecond() : "" + now.getSecond();

		console.setText(hour + ":" + minute + ":" + second + ((now.getHour() < 12) ? " AM" : " PM") + " : " + message + "\n" + console.getText());
	}

	/**
	 * Sets the min and max width and height of a region.
	 * @param width of the region.
	 * @param height of the region.
	 */
	public static void setRegionSize(Region item, int width, int height)
	{
		item.setMaxWidth(width);
		item.setMinWidth(width);
		item.setMaxHeight(height);
		item.setMinHeight(height);
	}

	/**
	 * Prompts the user with a window, which asks for a OK / Cancel input. OK
	 * will yield TRUE and Cancel will yield FALSE.
	 * @param message to be displayed.
	 * @return which button the user selected.
	 */
	public static boolean promptUser(String message)
	{
		ConfirmationBox warningWindow = new ConfirmationBox(stage.getOwner(), message);
		warningWindow.showAndWait();
		return warningWindow.isSelected();
	}
}
