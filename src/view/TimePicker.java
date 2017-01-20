package view;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import model.Tools;

import java.security.InvalidParameterException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Project Name: FishingBot
 * Author: Kevin
 * Date: Dec 24, 2016
 * Description:
 * Local Time Spinner to select a given time of the day.
 */
public class TimePicker extends Spinner<LocalTime>
{
    /** Mode Enum to reflect which section of the time the user is changing. */
    enum Mode
    {
        HOUR("hh"),
        MINUTE("mm"),
        SECOND("ss"),
        MERDIEM("a");

        private final String format;

        Mode(final String format)
        {
            this.format = format;
        }

        /**
         * Generates a DateTimeFormatter specific to these Modes.
         * @param desiredModes - Modes which will be used for the TimePicker's display.
         * @return - DateTimeFormatter to display the Modes.
         */
        private static DateTimeFormatter forModes(final Mode... desiredModes)
        {
            final StringBuilder builder = new StringBuilder();

            for (final Mode desiredMode : desiredModes)
            {
                if (builder.length() > 0) { builder.append(desiredMode == MERDIEM ? ' ' : ':'); }
                builder.append(desiredMode.format);
            }

            return DateTimeFormatter.ofPattern(builder.toString());
        }
    }

    /** Mode of which section of the clock gets increased. */
    private final ObjectProperty<Mode> propertyMode;
    /** Formatter to output the date and time as a String. */
    private final DateTimeFormatter dtfFormatter;
    /** The textfield inside the Spinner. */
    private final TextField txfEdit;
    /** Properties of the actual representation of this Spinner's time. */
    private final ObjectProperty<LocalTime> timeProperty;
    /** Map of where a given Mode corresponds to a range of indexes for the TextField. */
    private final TreeMap<Mode, IndexRange> mapModeRanges = new TreeMap<>();

    /**
     * Create TimePicker with only specific Modes.
     * For example, a TimePicker will only display AM/PM
     * if desiredModes has only the Mode.MERDIEM element.
     * @param lt - LocalTime as the default value of the TextField.
     * @param desiredModes - Modes in which the TimePicker will include.
     * @throws InvalidParameterException - If desiredModes is empty.
     */
    public TimePicker(final LocalTime lt, final Mode... desiredModes) throws InvalidParameterException
    {
        if (desiredModes.length == 0)
            throw new InvalidParameterException("No provided desired modes!");
        /* Sort and grab only the unique Modes from what is provided. */
        TreeSet<Mode> modes = new TreeSet<>(Arrays.asList(desiredModes));
        int counter = 0;
        /* Associate each Mode with a range of indexes. */
        for (final Iterator<Mode> i = modes.iterator(); i.hasNext(); counter++)
            mapModeRanges.put(i.next(), new IndexRange(counter * 3, 2 + counter * 3));
        dtfFormatter = Mode.forModes(modes.toArray(new Mode[0]));
        propertyMode = new SimpleObjectProperty<>(mapModeRanges.firstKey());

        /* Assign the provided time to our properties. */
        timeProperty = new SimpleObjectProperty<>(lt);

        /* Editor settings. */
        txfEdit = this.getEditor();
        txfEdit.setAlignment(Pos.CENTER);
        txfEdit.setText(dtfFormatter.format(lt));

        /* When the user clicks on the TextField */
        txfEdit.setOnMouseClicked(e ->
        {
            final Set<Map.Entry<Mode, IndexRange>> entrySet = mapModeRanges.entrySet();
            for (final Map.Entry<Mode, IndexRange> entry : entrySet)
            {
                final int caret = txfEdit.getCaretPosition();
                final IndexRange r = entry.getValue();
                if (caret >= r.getStart() && caret <= r.getEnd())
                {
                    updateMode(entry.getKey());
                    e.consume();
                    return;
                }
            }
        });

        /* When the TextField is changed, the caret is reset. Re-update it. */
        txfEdit.textProperty().addListener(((observable1, oldValue1, newValue1) -> updateMode(propertyMode.get())));

        /* Key press listener which will move the selection left or right. */
        setOnKeyPressed(e ->
        {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT)
            {
                /* If the user pressed right, proceed to the rightward Mode.
                 * If the user pressed left, proceed to the leftward Mode.
                 * If there is no rightward or leftward, wrap around. */
                Mode newValue;
                if (e.getCode() == KeyCode.LEFT)
                {
                    newValue = mapModeRanges.lowerKey(propertyMode.get());
                    if (newValue == null) newValue = mapModeRanges.lastKey();
                }
                else
                {
                    newValue = mapModeRanges.higherKey(propertyMode.get());
                    if (newValue == null) newValue = mapModeRanges.firstKey();
                }

                updateMode(newValue);
                /* Consume the key press event to stop focus traversal. */
                e.consume();
            }
        });

        /* Allow the user to use the scroll wheel to change values. */
        setOnScroll(e ->
        {
            if (e.getDeltaY() > 0) increment();
            else if (e.getDeltaY() < 0) decrement();
        });

        /* ValueFactory -- Controls all adding, subtracting, and displaying of the Spinner Value. */
        final SpinnerValueFactory<LocalTime> svfFactory = new SpinnerValueFactory<LocalTime>()
        {
            public void change(final boolean add, final int steps)
            {
                /* Prevent the caret listener from triggering during this process. */
                final LocalTime oldTime = timeProperty.get(), newTime;
                switch (propertyMode.get())
                {
                    case HOUR: newTime = add ? oldTime.plusHours(steps) : oldTime.minusHours(steps); break;
                    case MINUTE: newTime = add ? oldTime.plusMinutes(steps) : oldTime.minusMinutes(steps); break;
                    case SECOND: newTime = add ? oldTime.plusSeconds(steps) : oldTime.minusSeconds(steps); break;
                    default:
                        /* AM/PM - An even number of steps means AM/PM will not change. */
                        newTime = (steps % 2 != 0) ? oldTime.plusHours(12) : oldTime; break;
                }
                timeProperty.set(newTime);
                setValue(newTime);
            }

            @Override
            public void decrement(int steps)
            {
                change(false, steps);
            }

            @Override
            public void increment(int steps)
            {
                change(true, steps);
            }
        };
        svfFactory.setValue(lt);

        /* Show the factory how to display our LocalTime object. */
        svfFactory.setConverter(new StringConverter<LocalTime>()
        {
            @Override
            public String toString(LocalTime object)
            {
                return dtfFormatter.format(object);
            }

            @Override
            public LocalTime fromString(String string)
            {
                return LocalTime.parse(string);
            }
        });
        setValueFactory(svfFactory);

        /* Implement the ability for  */
        final PressHoldEventHandler evhPressHold = new PressHoldEventHandler();
        addEventFilter(MouseEvent.MOUSE_PRESSED, evhPressHold);
        addEventFilter(MouseEvent.MOUSE_RELEASED, e ->
        {
            if (e.getButton() == MouseButton.PRIMARY) evhPressHold.stop();
        });
    }

    /**
     * Create TimePicker with only specific Modes.
     * For example, a TimePicker will only display AM/PM
     * if desiredModes has only the Mode.MERDIEM element.
     * @param desiredModes - Modes which the TimePicker will include.
     * @throws InvalidParameterException - If desiredModes is empty.
     */
    public TimePicker(final Mode... desiredModes)
    {
        this(LocalTime.now(), desiredModes);
    }

    public TimePicker(final LocalTime time)
    {
        this(time, Mode.values());
    }

    public TimePicker()
    {
        this(LocalTime.now());
    }

    /**
     * Updates the current Mode of the TimePicker.
     * Uses Platform.runLater instead of just calling `selectRange`
     * to avoid the event handler that is calling `selectRange` from
     * re-updating the method after an update has just occured.
     * @param newValue - Mode in which the TimePicker is switching to.
     */
    private void updateMode(final Mode newValue)
    {
        final IndexRange range = mapModeRanges.get(newValue);
        Platform.runLater(() -> txfEdit.selectRange(range.getStart(), range.getEnd()));
        if (propertyMode.get() != newValue)
            propertyMode.set(newValue);
    }

    /* CSS Pseudo class which is used to indicate custom 'press and hold' event via the PressHoldEventHandler. */
    private static final PseudoClass PRESSED = PseudoClass.getPseudoClass("pressed");

    /* Event handler for custom 'press and hold' events for the Spinner.
    *  Named class is necessary as custom methods are inside of this inner class. */
    private final class PressHoldEventHandler implements EventHandler<MouseEvent>
    {
        /** Whether or not to increment or decrement during the hold event. */
        private boolean increment;
        private long startTS_ms;
        private Node ndeButton;

        /* Delay of pressing the button to when the changing of value goes faster. */
        private static final long HOLD_DELAY = 550L, CHANGE_DELAY = 40L;

        private final AnimationTimer timer = new AnimationTimer()
        {
            @Override
            public void handle(final long now)
            {
                if (Tools.timePassed(startTS_ms, HOLD_DELAY))
                {
                    /* Once the press delay is over, trigger events once per frame. */
                    if (increment) increment();
                    else decrement();
                    Tools.sleep(CHANGE_DELAY);
                }
            }
        };

        /* Stops the animation timer. */
        public void stop()
        {
            /* Prevent stop from being called if the timer isn't running. */
            if (ndeButton == null)
                return;
            timer.stop();
            ndeButton.pseudoClassStateChanged(PRESSED, false);
            ndeButton = null;
        }

        @Override
        public void handle(MouseEvent event)
        {
            /* Only allow mouse Button presses.
             * Reject any AM/PM changes since there is no need for fast scrolling. */
            if (event.getButton() != MouseButton.PRIMARY || propertyMode.get() == Mode.MERDIEM)
                return;
            Node node = event.getPickResult().getIntersectedNode();

            Boolean increment = null;
            /* Attempt to locate which button was pressed -- Up or down. */
            while (increment == null && node != TimePicker.this)
            {
                if (node.getStyleClass().contains("increment-arrow-button")) increment = true;
                else if (node.getStyleClass().contains("decrement-arrow-button")) increment = false;
                else node = node.getParent();
            }

            /* If the increment or decrement arrows could not be located, ignore this event. */
            if (increment == null)
                return;
            TimePicker.this.requestFocus();
            /* Tell the animation timer whether or not to increase or decrease. */
            this.increment = increment;
            /* Starting timestamp for the delay. */
            startTS_ms = System.currentTimeMillis();
            /* Setup pointer to the button so we can later change the CSS state. */
            ndeButton = node;
            /* Indicate to the stylesheets that the Button was pressed. */
            node.pseudoClassStateChanged(PRESSED, true);
            /* Perform one increment, and wait the delay time. */
            timer.handle(startTS_ms + HOLD_DELAY);
            /* Begin the incrementation loop. */
            timer.start();
        }
    }

    public ObjectProperty<LocalTime> timeProperty()
    {
        return timeProperty;
    }

    public LocalTime getTime()
    {
        return timeProperty.get();
    }
}
