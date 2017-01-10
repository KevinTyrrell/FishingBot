package model;

import controller.Controller;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.TimeUnit;

/**
 * Project Name: FishingBot
 * Author: Kevin
 * Date: Nov 22, 2016
 * Description:
 * Represents a Lure in-game.
 */
public final class Lure
{
    /** The type of Lure that is currently selected. */
    private ObjectProperty<LureType> type = new SimpleObjectProperty<>();
    /** Timestamp of when a lure was last applied to the fishing pole. */
    private Long applyTS = null;
    /** The amount of lures left by the user for FishingBot to use. */
    private ObjectProperty<Integer> quantityProperty = new SimpleObjectProperty<>();

    public Lure(final ObjectProperty<Integer> valueProperty)
    {
        /* Sync the quantity here with the quantity inside the NumberField. */
        quantityProperty.bindBidirectional(valueProperty);
        /* If the user changes the Lure, notify him of what has changed. */
        type.addListener((observable, oldValue, newValue) ->
        {
            if (oldValue == null) Controller.sendMessage(Lang.EN_MSG_LURE_SET.concat(newValue.getName()));
            else if (newValue == null) Controller.sendMessage(Lang.EN_MSG_LURE_REMOVE);
            else Controller.sendMessage(Lang.EN_MSG_LURE_CHANGED.concat(newValue.getName()));
        });
    }

    /**
     * Applies the selected lure to the user's fishing pole.
     */
    public synchronized void apply()
    {
        final int LURE_SLEEP = 7000;
        Controller.sendMessage(Lang.EN_MSG_LURE_APPLY.concat(getType().getName()).concat("."));
        Tools.typeStr(Lang.EN_USE.concat(getType().getName()));
        Tools.typeStr(Lang.EN_GRAB_POLE);
        /* Deduct one lure from the user's total quantity. */
        final Integer lures = quantityProperty.get();
        if (lures != null)
            quantityProperty.set(lures - 1 > 0 ? lures - 1 : null);
        applyTS = System.currentTimeMillis();
        Tools.sleep(Tools.fluctuate(LURE_SLEEP));
    }

    /**
     * Determine if a lure needs to be applied.
     * A lure can only be applied under the following conditions:
     * - A lure choice has been selected on the GUI.
     * - The previous lure has faded away from the character.
     * - The user has enough lures left, if provided this information.
     * @return - True if a lure should be applied right now, false if otherwise.
     */
    public synchronized boolean shouldApply()
    {
        final Integer luresLeft = quantityProperty.get();
        return type.get() != null
                && (applyTS == null || Tools.timePassed(applyTS, TimeUnit.MINUTES.toMillis(type.get().getDuration())))
                && (luresLeft == null || luresLeft > 0);
    }

    public synchronized LureType getType()
    {
        return type.get();
    }

    public synchronized ObjectProperty<LureType> typeProperty()
    {
        return type;
    }

    public boolean isOutOfLures()
    {
        return quantityProperty.get() <= 0;
    }

    public Integer getQuantity()
    {
        return quantityProperty.get();
    }

    public ObjectProperty<Integer> quantityProperty()
    {
        return quantityProperty;
    }
}
