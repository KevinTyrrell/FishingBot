package view;

import static localization.Lang.*;
import static localization.Lang.Locale.*;

import controller.Controller;
import controller.Conversation;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import localization.Macros;
import model.LureType;

import java.io.File;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;

public final class GUI extends Application
{
    @Override
    public void start(final Stage primaryStage) throws InterruptedException
    {
        /* Convenience variables. */
        final int WINDOW_WIDTH = 475, WINDOW_HEIGHT = 550;

        /* External windows for the program. */
        // TODO: Implement.
        //final SelectionWindow winSelect = new SelectionWindow(primaryStage);
        //final ExtraOptionWindow winOptions = new ExtraOptionWindow(primaryStage);

        final Pane root = createRootPane(primaryStage);

        /* CSS stylesheets. */
        final Scene scene = new Scene(root);
        final URL stylesheet = getClass().getClassLoader().getResource(Macros.PATH_MAIN_STYLESHEET);
        if (stylesheet != null)
            scene.getStylesheets().add(stylesheet.toExternalForm());
        else System.err.println("Unable to locate stylesheet: ".concat(Macros.PATH_MAIN_STYLESHEET));
        //lblConsole.setId("console-text");

        /* Window setup. */
        primaryStage.getIcons().add(new Image(new File("images/icon.png").toURI().toString()));
        primaryStage.setResizable(false);
        primaryStage.setTitle(LABEL_TITLE.get());
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private static Pane createRootPane(final Stage stage)
    {
        assert stage != null;

        /* Constants. */
        final double CIRCLE_FRAME_RADIUS = 35.0;

        /* Controls (without text). */
        final TextArea txaConsole = new TextArea();
        final ImageView imgQuest = new ImageView(),
                imgMouse = new ImageView();
        final Slider sldDelay = new Slider(),
                sldSensitivity = new Slider();
        final Circle cirFrame = new Circle(CIRCLE_FRAME_RADIUS);

        /* Controls (with text). */
        final Label lblConsole = new Label(),
                lblSpeed = new Label(),
                lblSensitivity = new Label();
        final ToggleButton tgbOnTop = new ToggleButton(),
                tgbDebug = new ToggleButton();
        final Button btnStart = new Button(),
                btnStop = new Button();

        /* If the active language changes, labels will update. */
        final Labeled[] labels = new Labeled[]{
                lblConsole, lblSpeed, lblSensitivity, tgbOnTop,
                tgbDebug, btnStart, btnStop
        };
        final Locale[] locales = new Locale[] {
                LABEL_CONSOLE, LABEL_SPEED, LABEL_SENSITIVITY,
                LABEL_ON_TOP, LABEL_DEBUG, LABEL_START, LABEL_STOP
        };
        assert labels.length == locales.length;
        for (int i = 0; i < labels.length; i++)
        {
            labels[i].textProperty().bind(Bindings.createStringBinding(
                    locales[i]::get, activeLanguageProperty()));
        }

        /* Panes. */
        final HBox hbxButtons = new HBox(btnStart, btnStop),
                hbxLures = new HBox();
        final GridPane grdOptions = new GridPane();
        final VBox vbxRoot = new VBox(lblConsole, txaConsole, hbxButtons, hbxLures, grdOptions);
        final StackPane stkFrame = new StackPane(cirFrame, imgQuest);

        /* Effects. */
        final InnerShadow insBrighten = new InnerShadow(
                BlurType.GAUSSIAN, Color.rgb(255, 255, 255, 0.15),
                10, 1.0, 100, 100);

        /* Event listeners and bindings. */
        btnStop.disableProperty().bind(btnStart.disabledProperty().not());
        btnStart.setOnAction(e -> btnStart.setDisable(Controller.INSTANCE.start()));
        btnStop.setOnAction(e -> btnStart.setDisable(Controller.INSTANCE.stop()));
        tgbOnTop.selectedProperty().addListener((observable, oldValue, newValue) -> stage.setAlwaysOnTop(newValue));

        /* Console attributes. */
        txaConsole.setEditable(false);
        txaConsole.setFocusTraversable(false);
        txaConsole.setWrapText(true);
        /* Connect the back-end's messages to the front end's console. */
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
        final Conversation.MessageListener listener = message ->
        {
            txaConsole.setScrollTop(Double.MIN_VALUE);
            txaConsole.appendText(String.format("%s: %s%s",
                    LocalTime.now().format(dateFormat), message, System.lineSeparator()));
        };
        Controller.INSTANCE.getMainConversation().listenIn(listener);
        Controller.INSTANCE.getDebugConversation().listenIn(listener);

        /* Lure section. */
        Arrays.stream(LureType.values()).forEach(e ->
                hbxLures.getChildren().add(new LureButton(e)));

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
        //cirFrame.setOnMouseClicked(e -> winOptions.showAndWait());

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

        /* Tooltips for all controls. */
        btnStart.setTooltip(new Tooltip(TOOLTIP_START.get()));
        btnStop.setTooltip(new Tooltip(TOOLTIP_STOP.get()));
        tgbOnTop.setTooltip(new Tooltip(TOOLTIP_ON_TOP.get()));
        tgbDebug.setTooltip(new Tooltip(TOOLTIP_DEBUG.get()));
        Tooltip.install(cirFrame, new Tooltip(TOOLTIP_OPTIONS.get()));

        return vbxRoot;
    }

    /**
     * Main method. Launch(args) searches for the method START in this class.
     * @param args from command line.
     */
    public static void main(String[] args)
    {
        assert args != null;
        launch(args);
    }
}
