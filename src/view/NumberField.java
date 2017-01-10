package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

/**
 * Project Name: FishingBot
 * Author: Kevin
 * Date: Jan 09, 2017
 * Description:
 * NumberField only allows integer inputs which are between the ranges of MAX and MIN.
 * Behaves very similar to a Spinner.
 * Has functionality for Arrow Key presses and scroll wheels.
 * Allows for blank input which would mean the Value becomes null.
 */
public class NumberField extends TextField
{
    /* Minimum and maximum values that the Textfield can go to. */
    private final int minValue, maxValue;
    /* Value is an atomic integer so we can use NULL as a value. */
    private final ObjectProperty<Integer> valueProperty;
    /* Flag to prevent listeners from triggering accidentally. */
    private boolean ignoreFlag = false;

    public NumberField(final int min, final int max, final Integer value)
    {
        minValue = min;
        maxValue = max;
        valueProperty = new SimpleObjectProperty<>(value);

        /* Converter for the TextField. */
        final StringConverter<Integer> cnvValue = new StringConverter<Integer>()
        {
            @Override
            public String toString(Integer object)
            {
                return (object != null) ? String.valueOf(object) : "";
            }

            @Override
            public Integer fromString(String string)
            {
                /* Delete any non-numerical characters AND leading zeroes. */
                string = string.replaceAll("[^\\d]", "").replaceFirst("^0+(?!$)", "");
                return (!string.isEmpty()) ? Integer.parseInt(string) : null;
            }
        };

        /* Monitor every change to this textfield. */
        final UnaryOperator<TextFormatter.Change> chgOperator = change ->
        {
            /* Grab the text that WOULD be what the Textfield has after this change. */
            final String text = change.getControlNewText();

            /* Reject changes if they are not blank or numbers. */
            if (!text.isEmpty() && !text.matches("[\\d]+"))
                return null;
            final Integer parsed = cnvValue.fromString(text);
            if (parsed != null && parsed < min && parsed > max)
                return null;

            /* Update the properties and approve the change. */
            ignoreFlag = true;
            valueProperty.set(parsed);
            ignoreFlag = false;

            return change;
        };

        /* Formatter will manage this text. */
        setTextFormatter(new TextFormatter<>(cnvValue, value, chgOperator));

        /* Scroll functionality. */
        setOnScroll(e ->
        {
            if (e.getDeltaY() > 0)
                increment();
            else if (e.getDeltaY() < 0)
                decrement();
        });

        setOnKeyPressed(e ->
        {
            boolean consume;
            if (consume = e.getCode() == KeyCode.UP)
                increment();
            else if (consume = e.getCode() == KeyCode.DOWN)
                decrement();
            if (consume)
                e.consume();
        });

        /* Allow the value to be changed outside of the textfield. */
        valueProperty.addListener((observable, oldValue, newValue) ->
        {
            if (ignoreFlag)
                return;
            change(newValue);
        });
    }

    public void increment()
    {
        final Integer value = valueProperty.get();
        change((value != null) ? Math.min(value + 1, maxValue) : minValue);

    }

    public void decrement()
    {
        final Integer value = valueProperty.get();
        change((value == null || value - 1 < minValue) ? null : value - 1);
    }

    private void change(final Integer newVal)
    {
        setText((newVal != null) ? String.valueOf(newVal) : "");
    }

    public Integer getValue()
    {
        return valueProperty.get();
    }

    public ObjectProperty<Integer> valueProperty()
    {
        return valueProperty;
    }

    public int getMaxValue()
    {
        return maxValue;
    }

    public int getMinValue()
    {
        return minValue;
    }
}
