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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
	private Button startBtn, endBtn, calibrateBtn, calibrateAreaBtn;
	public static ChoiceBox<String> lureCB;
	public static CheckBox lureCKB;
	private HBox buttonHB, lureHB;

	private final int SCENE_WIDTH = 450, SCENE_HEIGHT = 400;
	public static final String DEBUG_PANE_BORDER = "-fx-border-color: blue;\n" + "-fx-border-insets: 5;\n" + "-fx-border-width: 3;\n" + "-fx-border-style: dashed;\n";

	@Override public void start(Stage stage) throws InterruptedException
	{
		// Assign the stage as an instance variable.
		GUI.stage = stage;
		
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
		//promptUser("FishingBot has detected the following display: " + Logic.display.getWidth() + " x " + Logic.display.getHeight() + ".");
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
		startBtn = new Button("Run");
		startBtn.setTooltip(new Tooltip("Begin the fishing loop, once calibrated."));
		startBtn.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		startBtn.setOnAction(e ->
		{
			Logic.startFishing();
		});

		// End Fishing Button.
		endBtn = new Button("End");
		endBtn.setTooltip(new Tooltip("Ends the fishing loop, if it is running."));
		endBtn.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		endBtn.setOnAction(e ->
		{
			if (Logic.fishingActive)
			{
				// User wants to stop fishing.
				Logic.fishingActive = false;
				lureCB.setDisable(false);
				lureCKB.setDisable(false);
				consoleMessage("Fishing Mode: OFF");
			}
		});

		// Calibration Button.
		calibrateBtn = new Button("Calibrate");
		calibrateBtn.setTooltip(new Tooltip("Calibrate the program by manually casting\nyour bobber, and hovering your mouse over it."));
		calibrateBtn.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		calibrateBtn.setOnAction(e ->
		{
			// Loop should be on it's own thread so it doesn't freeze the GUI.
			new Thread(() ->
			{
				// Print whether the Calibration was successful.
				consoleMessage("Calibration was " + (Logic.calibrate() ? "successful" : "unsuccessful") + ".");
			}).start();
		});

		// HBox which houses the three main buttons.
		buttonHB = new HBox(startBtn, endBtn, calibrateBtn);
		buttonHB.setAlignment(Pos.CENTER);
		buttonHB.setSpacing(20);
		for (Node i : buttonHB.getChildren())
		{
			setRegionSize((Region) i, 125, 45);
		}
		
		// CheckBox for the user to activate bobber control.
		lureCKB = new CheckBox();
		lureCKB.setOnAction(e ->
		{
			lureCB.setValue(null);
			lureCB.setVisible(lureCKB.selectedProperty().get());
			startBtn.setDisable(lureCKB.isSelected());
		});
		
		// ChoiceBox for the type of lures.
		lureCB = new ChoiceBox<>();
		lureCB.getItems().addAll(
				"Aquadynamic Fish Attractor",
				"Aquadynamic Fish Lens",
				"Bright Baubles",
				"Flesh Eating Worm",
				"Nightcrawlers",
				"Shiny Bauble"
		);
		lureCB.setVisible(false);
		lureCB.setOnAction(e ->
		{
			// Allow the user to fish if he selects a bobber.
			startBtn.setDisable(false);
		});
		
		// HBox to house the lure controls in.
		lureHB = new HBox(lureCKB, lureCB);
		lureHB.setAlignment(Pos.CENTER);
				
		// Have a button for a custom search area for the calibration method.
		calibrateAreaBtn = new Button("Custom Calibrate Zone");
		calibrateAreaBtn.setOnAction(e ->
		{
			// Show a square to the user, and allow them to draw their own calibration points.
			SquareSelector box = new SquareSelector(scene.getWindow());
			Logic.topLeft = box.getTopLeft();
			Logic.bottomRight = box.getBottomRight();
		});

		// Add all the fields to the layout.
		root.getChildren().addAll(consoleTitle, console, buttonHB, lureHB, calibrateAreaBtn);

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
	private static void setRegionSize(Region item, int width, int height)
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
