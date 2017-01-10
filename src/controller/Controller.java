package controller;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import model.Angler;
import model.Lang;
import model.LureType;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Controller
{
    /** Prevent instantiation of the class. */
    private Controller() { }
    /** Angler for the GUI to interact with. */
    private static Angler fisherman;
    /** Second thread which is only used for fishing purposes. */
    private static Thread thdFishing = null;
    /** Listener for Model to send Strings to the user. */
    private static MessageListener listener = null;
    /** Prevents multiple function calls. */
    private static final AtomicBoolean busy = new AtomicBoolean(false);
    /** Variable for users to manipulate to debug their issues. */
    public static final BooleanProperty debugMode = new SimpleBooleanProperty(false);

    /**
     * Attempts to start the fishing loop.
     */
    public static void start()
    {
        /* Check if the user is allowed to start. */
        boolean error = true;
        if (thdFishing != null && thdFishing.isAlive())
            sendMessage(Lang.EN_ERROR_ALREADY_FISHING);
        else if (!fisherman.isCalibrated())
            sendMessage(Lang.EN_ERROR_NOT_CALIBRATED);
        else
            error = false;
        if (error) return;

        /* Start the fishing process. */
        sendMessage(Lang.EN_MSG_FISHING_START);
        Angler.interrupted = false;
        thdFishing = new Thread(fisherman);
        thdFishing.start();
    }

    /**
     * Attempts to stop the fishing loop.
     */
    public static void stop()
    {
        if (thdFishing == null || !thdFishing.isAlive())
        {
            sendMessage(Lang.EN_ERROR_NOT_FISHING);
            return;
        }

        sendMessage(Lang.EN_MSG_FISHING_END);
        thdFishing.interrupt();
    }

    public static void calibrate()
    {
        // TODO: This needs more thought.
        if (busy.get())
            return;
        busy.set(true);
        new Thread(() ->
        {
            /* Calibrate the program and output whether or not it was successful or not. */
            sendMessage(fisherman.calibrate() ? Lang.EN_MSG_CALIBRATE_SUCCESS : Lang.EN_MSG_CALIBRATE_FAIL);
            busy.set(false);
        }).start();
    }

    /**
     * Outputs a message to the MessageListener.
     * This message will include the timestamp using
     * localized time based on the user's System settings.
     * @param message - String to output.
     */
    public static void sendMessage(final String message)
    {
        if (listener != null)
            Platform.runLater(() -> listener.fire(
                            ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
                                    .concat(": ".concat(message))));
    }

    /**
     * Initializes the program.
     * @param btnDisableProperty - Property for disabling the Run, Stop, and Calibrate buttons.
     * @param lureProperty - The lure the user has selected via the front end.
     * @param scanSpeedProperty - The amount of delay the user wants via the front end.
     */
    public static void init(final BooleanProperty btnDisableProperty,
                            final ObjectProperty<LureType> lureProperty,
                            final DoubleBinding scanSpeedProperty,
                            final DoubleBinding sensitivityProperty,
                            final ObjectProperty<Integer> quantityProperty,
                            final ObjectBinding<LocalTime> timeBinding,
                            final BooleanProperty lureQuitProperty)
    {
        fisherman = new Angler(scanSpeedProperty, sensitivityProperty,
                btnDisableProperty, lureProperty,
                quantityProperty, timeBinding, lureQuitProperty);
    }

    /**
     * Sets the MessageListener variable so it can be used to fire events
     * to the GUI's console window.
     * @param listener - Listener to be used in firing events.
     */
    public static void setMsgListener(final MessageListener listener)
    {
        Controller.listener = listener;
    }
}
