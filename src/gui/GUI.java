package gui;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.time.LocalTime;

import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import program.*;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class GUI extends Application
{
    private Scene scene;
    private static Stage stage;
    private Label lblConsole, lblLureCount, lblLureLogout, lblTimeLogout;
    public static CheckBox chkLureLogout;
    private static TextArea txaConsole;
    public static TextField txfLureCount, txfTimeLogout;
    private HBox hbxButtons, hbxLures, hbxLureDetails;

    /** The dimensions of the application. */
    private final int SCENE_WIDTH = 450, SCENE_HEIGHT = 475;

    @Override
    public void start(Stage stage) throws InterruptedException
    {
        // Initialize core variables.
        GUI.stage = stage;
        try { Logic.pc = new Robot(); }
        catch (AWTException e)
        {
            // Cannot communicate with the PC. Exiting.
            promptUser("FishingBot was unable to communicate with your computer. Stack trace is as follows: " + e);
            System.exit(1);
        }

        // Root Pane.
        VBox root = new VBox(10);
        root.setPadding(new Insets(10, 10, 0, 10));
        root.setAlignment(Pos.TOP_CENTER);

        // Title for console.
        lblConsole = new Label("Console");
        lblConsole.setFont(Font.font("Verdana", 20));
        lblConsole.setTextFill(Color.web("#0076a3"));

        // Create the console.
        txaConsole = new TextArea();
        txaConsole.setEditable(false);
        setRegionSize(txaConsole, SCENE_WIDTH - 20, 200);

        // Start Fishing Button.
        Button btnStart = new Button("Run");
        btnStart.setTooltip(new Tooltip("Begin fishing, once calibration is complete."));
        btnStart.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
        btnStart.setOnAction(e ->
        {
            // Error checking -- do not allow the user to fish under some circumstances.
            if (Logic.calibrationPoint == null)
            {
                consoleMessage("You must calibrate the program before fishing!"); return;
            }
            else if (Logic.fishingActive) { consoleMessage("You are already fishing!"); return; }
            else if (Logic.selectedLure != null && txfLureCount.getText().isEmpty())
            {
                consoleMessage("You must input how many bobbers of this type you currently have!");
            }

            // Error checking over -- user can now fish.
            disableNodes(true, txfLureCount, txfTimeLogout, chkLureLogout);
            consoleMessage("Fishing Mode: ON");
            Logic.startFishing();
        });

        // End Fishing Button.
        Button btnEnd = new Button("End");
        btnEnd.setTooltip(new Tooltip("Pauses the fishing cycle."));
        btnEnd.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
        btnEnd.setOnAction(e -> {
            // Error checking.
            if (!Logic.fishingActive) { consoleMessage("You are not currently fishing!"); return; }

            // User wants to stop fishing.
            Logic.fishingActive = false;
            consoleMessage("Fishing Mode: OFF");
            disableNodes(false, txfLureCount, txfTimeLogout, chkLureLogout);
        });

        // Calibration Button.
        Button btnCalibrate = new Button("Calibrate");
        btnCalibrate.setTooltip(new Tooltip("Attempts to locate where your \"Fishing Bobber\" tooltip is.\nRight click this to draw a new search area."));
        btnCalibrate.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
        btnCalibrate.setOnMousePressed(e ->
        {
            if (e.getButton() == MouseButton.PRIMARY)
            {
                // Loop should be on it's own thread so it doesn't freeze the GUI.
                new Thread(() -> {
                    // Print whether the Calibration was successful.
                    consoleMessage("Calibration " + (Logic.calibrate() ? "was successful." :
                            "could not locate your Fishing Bobber tooltip!"));
                }).start();
            }
            else if (e.getButton() == MouseButton.SECONDARY)
            {
                // Show a square to the user, and allow them to draw their own calibration points.
                SquareSelector box = new SquareSelector(scene.getWindow());
                Logic.topLeft = box.getTopLeft();
                Logic.bottomRight = box.getBottomRight();
            }
        });

        // HBox which houses the three main buttons.
        hbxButtons = new HBox(btnStart, btnEnd, btnCalibrate);
        hbxButtons.setAlignment(Pos.CENTER);
        hbxButtons.setSpacing(20);
        for (Node i : hbxButtons.getChildren()) { setRegionSize((Region) i, 125, 45); }

		/*
        LURE SECTION:
		Consists of six icons for the user to interact with.
		The 'selected' lure is the one that will be used in-game.
	    Each lure has it's own time that it lasts for.
		 */
        // HBox to store our ImageView objects.
        hbxLures = new HBox(5);
        hbxLures.setAlignment(Pos.CENTER);

        // Go through all the different lures and add them to our HBox.
        for (Lure i : Lure.values())
        {
            Image imgLure = new Image(new File("res/" + i.getImage()).toURI().toString());
            ImageView imgvwLure = new ImageView(imgLure);

            // Event handlers -- Don't allow changes while fishing.
            imgvwLure.setOnMouseEntered(e -> {
                if (!Logic.fishingActive)
                {
                    imgvwLure.setScaleX(1.1);
                    imgvwLure.setScaleY(1.1);
                }
            });
            imgvwLure.setOnMouseExited(e -> {
                if (!Logic.fishingActive)
                {
                    imgvwLure.setScaleX(1);
                    imgvwLure.setScaleY(1);
                }
            });
            imgvwLure.setOnMousePressed(e ->
            {
                if (!Logic.fishingActive)
                {
                    // If a lure is already selected...
                    if (Logic.selectedLure != null)
                    {
                        // Erase all highlighting effects currently present.
                        for (Node h : hbxLures.getChildren()) h.setEffect(null);
                    }

                    // Reset the effect and pointer if double press, otherwise enable glow.
                    imgvwLure.setEffect((Logic.selectedLure == i) ? null : new InnerShadow(30.0, 2.0f, 2.0f, Color.GOLDENROD));
                    Logic.selectedLure = ((Logic.selectedLure == i) ? null : i);

                    // Set visibility of certain elements.
                    lblLureCount.setVisible(Logic.selectedLure != null);
                    txfLureCount.setVisible(Logic.selectedLure != null);
                    txfLureCount.setText("");
                    lblLureLogout.setVisible(false);
                    chkLureLogout.setVisible(false);
                    chkLureLogout.setSelected(false);
                }
            });
            Tooltip.install(imgvwLure, new Tooltip(i.getName()));

            imgvwLure.setOpacity(0.8);
            hbxLures.getChildren().add(imgvwLure);
        }

        /*
        Extra options below.
        Options consist of the following:
        Lure control, allowing user to say how many lures he has.
        Logout conditions; program will exit and will logout in-game if out of lures.
         */
        lblLureCount = new Label("Lures Remaining: ");
        lblLureCount.setFont(Font.font("Verdana", 14));
        lblLureCount.setTextFill(Color.web("#0076a3"));
        lblLureCount.setVisible(false);
        txfLureCount = new TextField();
        txfLureCount.setAlignment(Pos.CENTER);
        txfLureCount.setPromptText("Ex. 5");
        setRegionSize(txfLureCount, 45, 20);
        txfLureCount.setVisible(false);
        applyTextFormatter(txfLureCount, ".*[^0-9].*", 3);
        txfLureCount.textProperty().addListener(e ->
        {
            lblLureLogout.setVisible(!(txfLureCount.getText().isEmpty()));
            chkLureLogout.setVisible(!(txfLureCount.getText().isEmpty()));
        });

        hbxLureDetails = new HBox(4, lblLureCount, txfLureCount);
        hbxLureDetails.setPadding(new Insets(10, 10, 0, 10));
        hbxLureDetails.setAlignment(Pos.TOP_LEFT);

        // CheckBox for logout options.
        lblLureLogout = new Label("Logout/Exit at 0 Lures?");
        lblLureLogout.setPadding(new Insets(0, 0, 0, 60));
        lblLureLogout.setFont(Font.font("Verdana", 12));
        lblLureLogout.setTextFill(Color.web("#0076a3"));
        lblLureLogout.setOnMouseClicked(e ->
        {
            // Clicking on the label will click on the CheckBox.
            chkLureLogout.setSelected(!chkLureLogout.isSelected());
            chkLureLogout.requestFocus();
        });
        chkLureLogout = new CheckBox();
        chkLureLogout.setVisible(false);
        lblLureLogout.setVisible(false);
        hbxLureDetails.getChildren().addAll(lblLureLogout, chkLureLogout);

        // Time-based logout.
        lblTimeLogout = new Label("Time to Logout?:");
        lblTimeLogout.setFont(Font.font("Verdana", 14));
        lblTimeLogout.setTextFill(Color.web("#0076a3"));
        txfTimeLogout = new TextField();
        setRegionSize(txfTimeLogout, 45, 20);
        txfTimeLogout.setPromptText("mins.");
        txfTimeLogout.setAlignment(Pos.CENTER);
        applyTextFormatter(txfTimeLogout, ".*[^0-9].*", 4);

        HBox hbxTimeLogout = new HBox(10, lblTimeLogout, txfTimeLogout);
        hbxTimeLogout.setPadding(new Insets(10, 10, 0, 10));
        hbxTimeLogout.setAlignment(Pos.TOP_LEFT);

        // Add all the fields to the layout.
        root.getChildren().addAll(lblConsole, txaConsole, hbxButtons, hbxLures, hbxTimeLogout, hbxLureDetails);

        scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        stage.getIcons().add(new Image(new File("res/fbicon.png").toURI().toString()));
        stage.setResizable(false);
        stage.setTitle("FishingBot");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method. Launch(args) searches for the method START in this class.
     * @param args from command line.
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

        String hour, minute, second;
        hour = (now.getHour() > 12) ? "" + (now.getHour() % 12) : "" + now.getHour();
        minute = (now.getMinute() < 10) ? "0" + now.getMinute() : "" + now.getMinute();
        second = (now.getSecond() < 10) ? "0" + now.getSecond() : "" + now.getSecond();

        txaConsole.setText(hour + ":" + minute + ":" + second + ((now.getHour() < 12) ? " AM" : " PM") + " : " + message + "\n" + txaConsole.getText());
    }

    /**
     * Applies a text formatter to this control.
     * This formatter prevents certain inputs depending on the given parameters.
     * @param control to be applied to.
     * @param regex to be used.
     * @param maxLength to be used.
     */
    private static void applyTextFormatter(TextInputControl control, String regex, int maxLength)
    {
        control.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) ->
        {
            // The text that is going to be applied...
            String newText = change.getControlNewText();
            // Rejects the input if it is too long or matches the given regex.
            return (newText.length() > maxLength || newText.matches(regex)) ? null : change;
        }));
    }

    /**
     * Sets the min and max width and height of a region.
     * @param width  of the region.
     * @param height of the region.
     */
    private static void setRegionSize(Region item, int width, int height)
    {
        item.setMaxSize(width, height);
        item.setMinSize(width, height);
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

    /**
     * Disables or enables one or more Node objects.
     * @param disableProperty enable or disable.
     * @param nodes to be affected.
     */
    private static void disableNodes(boolean disableProperty, Node... nodes)
    {
        for (Node i : nodes) { i.setDisable(disableProperty); }
    }
}
