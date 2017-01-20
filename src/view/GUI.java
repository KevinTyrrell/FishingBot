package view;

import controller.Controller;
import controller.SaveData;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Lang;
import model.LureType;

import java.io.File;

public class GUI extends Application
{
    /** Selection Window for calibration uses. */
    public static SelectionWindow winSelect;
    public static ExtraOptionWindow winOptions;

    @Override
    public void start(final Stage primaryStage) throws InterruptedException
    {
        /* Convenience variables. */
        final int WINDOW_WIDTH = 475, WINDOW_HEIGHT = 550;
        final double CIRCLE_FRAME_RADIUS = 35.0;
        final String STYLESHEET_PATH = "/Style.css";

        /* Nodes. */
        final Label lblConsole = new Label(Lang.EN_NODE_CONSOLE),
                lblSpeed = new Label(Lang.EN_LABEL_SPEED),
                lblSensitivity = new Label(Lang.EN_LABEL_SENSITIVITY);
        final TextArea txaConsole = new TextArea();
        final ToggleButton tgbOnTop = new ToggleButton(Lang.EN_LABEL_ON_TOP),
                tgbDebug = new ToggleButton(Lang.EN_LABEL_DEBUG);
        final ImageView imgQuest = new ImageView(),
                imgMouse = new ImageView();
        final Button btnStart = new Button(Lang.EN_NODE_START),
                btnStop = new Button(Lang.EN_NODE_STOP),
                btnCalibrate = new Button(Lang.EN_NODE_CALIBRATE, imgMouse);
        final Slider sldDelay = new Slider(),
                sldSensitivity = new Slider();
        final Circle cirFrame = new Circle(CIRCLE_FRAME_RADIUS);

        /* Panes. */
        final HBox hbxButtons = new HBox(btnStart, btnStop, btnCalibrate),
                hbxLures = new HBox();
        final GridPane grdOptions = new GridPane();
        final VBox vbxRoot = new VBox(lblConsole, txaConsole, hbxButtons, hbxLures, grdOptions);
        final StackPane stkFrame = new StackPane(cirFrame, imgQuest);

        /* Effects. */
        final InnerShadow insBrighten = new InnerShadow(
                BlurType.GAUSSIAN, Color.rgb(255, 255, 255, 0.15), 10, 1.0, 100, 100);

        /* External windows for the program. */
        winSelect = new SelectionWindow(primaryStage);
        winOptions = new ExtraOptionWindow(primaryStage);

        /* Event listeners. */
        btnStart.setOnAction(e -> Controller.start());
        btnStop.setOnAction(e -> Controller.stop());
        btnCalibrate.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
        {
            if (event.isSecondaryButtonDown())
                winSelect.showAndWait();
            else
                Controller.calibrate();
        });
        btnStop.disableProperty().bind(btnStart.disableProperty().not());
        btnCalibrate.disableProperty().bind(btnStart.disabledProperty());
        Controller.debugMode.bind(tgbDebug.selectedProperty());
        tgbOnTop.selectedProperty().addListener((observable, oldValue, newValue) -> primaryStage.setAlwaysOnTop(newValue));

        /* Console attributes. */
        txaConsole.setEditable(false);
        txaConsole.setFocusTraversable(false);
        txaConsole.setWrapText(true);
        Controller.setMsgListener(message ->
        {
            txaConsole.setScrollTop(Double.MIN_VALUE);
            txaConsole.appendText(message.concat(System.lineSeparator()));
        });

        /* Lure section. */
        final ObjectProperty<ImageView> prpIvwLure = new SimpleObjectProperty<>();
        final ObjectProperty<LureType> prpCurrentLure = new SimpleObjectProperty<>();
        for (final LureType i : LureType.values())
        {
            final Image imgLure = new Image(ClassLoader.class.getResourceAsStream("/".concat(i.getPath())));
            final ImageView ivwLure = new ImageView(imgLure);
            ivwLure.setPreserveRatio(true);
            /* Scale the size of lures down depending on how many lures there are. */
            ivwLure.setFitHeight(Math.min(imgLure.getHeight() * 6 / LureType.values().length, imgLure.getHeight() * 1.25));

            /* Allow only one activated lure at a time. */
            ivwLure.setOnMousePressed(e ->
            {
                /* The lure user clicked on is different than the one he already had enabled. */
                if (ivwLure != prpIvwLure.get())
                {
                    /* Cancel the glow on the previous ImageView. */
                    if (prpIvwLure.get() != null) prpIvwLure.get().setId(null);
                    ivwLure.setId(Lang.CSS_LURE_GLOW);
                    prpIvwLure.set(ivwLure);
                    prpCurrentLure.set(i);
                }
                else
                {
                    ivwLure.setId(null);
                    prpIvwLure.set(null);
                    prpCurrentLure.set(null);
                }
            });
            Tooltip ttpName = new Tooltip(i.getName());
            Tooltip.install(ivwLure, ttpName);
            hbxLures.getChildren().add(ivwLure);
        }

        /* Extra Options section. */
        sldDelay.setValue(sldDelay.getMax() * 0.75);
        sldSensitivity.setValue(sldSensitivity.getMax() * 0.35);
        hbxLures.spacingProperty().bind(new SimpleIntegerProperty(LureType.values().length));

        /* Mouse graphic. */
        imgMouse.setId("mouse");
        imgMouse.setPreserveRatio(true);
        imgMouse.setFitHeight(30);

        /* Quest GIF Section */
        imgQuest.setId("quest");
        imgQuest.setPreserveRatio(true);
        imgQuest.setFitHeight(CIRCLE_FRAME_RADIUS * 2 * 0.95);
        imgQuest.setMouseTransparent(true);
        /* Brighten the Node when the user hovers over it. */
        cirFrame.setOnMouseEntered(e -> ((Node) e.getSource()).setEffect(insBrighten));
        cirFrame.setOnMouseExited(e -> ((Node) e.getSource()).setEffect(null));
        cirFrame.setOnMouseClicked(e -> winOptions.showAndWait());

        /* Attempt to load saved data from file. */
        final SaveData save = SaveData.load();
        if (save != null)
            save.sync(sldDelay.valueProperty(), sldSensitivity.valueProperty(),
                    tgbOnTop.selectedProperty(), tgbDebug.selectedProperty());

        /* Gridpane. */
        final ColumnConstraints colOne = new ColumnConstraints(120),
                colTwo = new ColumnConstraints(200);
        grdOptions.getColumnConstraints().addAll(colOne, colTwo);
        colTwo.setHalignment(HPos.CENTER);
        int row = 0;
        grdOptions.add(lblSpeed, 0, row);
        grdOptions.add(sldDelay, 1, row++, 3, 1);
        grdOptions.add(lblSensitivity, 0, row);
        grdOptions.add(sldSensitivity, 1, row++, 3, 1);
        grdOptions.addRow(row, tgbOnTop, stkFrame, tgbDebug);

        /* Initialize sync with back-end and front-end. */
        Controller.init(btnStart.disableProperty(),
                prpCurrentLure,
                sldDelay.valueProperty().negate().add(sldDelay.getMax()),
                sldSensitivity.maxProperty().subtract(sldSensitivity.valueProperty()).divide(10),
                winOptions.numLures.valueProperty(),
                Bindings.when(winOptions.chkQuitTime.selectedProperty())
                        .then(winOptions.tpkLogoutTime.timeProperty())
                        .otherwise(new SimpleObjectProperty<>(null)),
                winOptions.chkQuitLures.selectedProperty());

        /* Tooltips for all controls. */
                btnStart.setTooltip(new Tooltip(Lang.EN_TOOLTIP_START));
        btnStop.setTooltip(new Tooltip(Lang.EN_TOOLTIP_STOP));
        btnCalibrate.setTooltip(new Tooltip(Lang.EN_TOOLTIP_CALIBRATE));
        tgbOnTop.setTooltip(new Tooltip(Lang.EN_TOOLTIP_ON_TOP));
        tgbDebug.setTooltip(new Tooltip(Lang.EN_TOOLTIP_DEBUG));
        Tooltip.install(cirFrame, new Tooltip(Lang.EN_TOOLTIP_EXTRA_OPTIONS));

        /* CSS stylesheets. */
        final Scene scene = new Scene(vbxRoot);
        scene.getStylesheets().add(getClass().getResource(STYLESHEET_PATH).toExternalForm());
        lblConsole.setId("console-text");

        /* If the window is closing, save any data. */
        primaryStage.setOnCloseRequest(event -> SaveData.save(
                new SaveData(sldDelay.valueProperty(), sldSensitivity.valueProperty(),
                        tgbOnTop.selectedProperty(), tgbDebug.selectedProperty())));
        primaryStage.getIcons().add(new Image(new File("icon.png").toURI().toString()));
        primaryStage.setResizable(false);
        primaryStage.setTitle(Lang.EN_TITLE);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method. Launch(args) searches for the method START in this class.
     * @param args from command line.
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
