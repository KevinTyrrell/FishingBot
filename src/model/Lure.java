/*
 * Copyright 2017 Kevin Tyrrell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package model;

import controller.Controller;
import javafx.beans.property.*;
import localization.Lang;
import localization.Macros;
import model.singleton.Computer;
import view.LureButton;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/*
 * Name: Kevin Tyrrell
 * Date: 11/22/2016
 */
public final class Lure
{
    /** The type of Lure that is currently selected. */
    private final ObjectProperty<LureType> type;
    /** The amount of lures left by the user for FishingBot to use. */
    private final IntegerProperty quantity;
    /** Flag which determines if a lure is isReady to be applied. */
    private final BooleanProperty ready;
    
    /** Variables used to describe lure quantity. */
    private static final int INFINITE = -1, EMPTY = 0;

    public Lure()
    {
        type = new SimpleObjectProperty<>();
        type.bind(LureButton.getTypeBinding());
        type.addListener((observable, oldValue, newValue) ->
        {
            Controller.INSTANCE.getMainConversation()
                    .whisper(String.format(Lang.Locale.MSGF_LURE_SWITCHED.get(), oldValue.toString(), ));
        });
        quantity = new SimpleIntegerProperty();
        ready = new SimpleBooleanProperty(true);
    }

    /**
     * Applies the currently selected lure to the user's fishing pole.
     * Forbids another lure from being applied for the duration of the lure.
     */
    public void apply()
    {
        assert isReady();
        
        /* Indicate that the lure is no longer isReady to be applied. */
        ready.set(false);
        
        Computer.INSTANCE.type(Macros.USE.concat(type.get().getName().get()));
        AlarmClock.nap(TimeUnit.MILLISECONDS, 400);
        Computer.INSTANCE.type(Macros.GRAB_POLE);
        
        final int lures = quantity.get();
        if (lures != INFINITE)
            quantity.set(lures - 1 > EMPTY ? lures - 1 : EMPTY);
                
        /* Create an alarm to signal the next lure application. */
        AlarmClock.nap(TimeUnit.SECONDS, 8);
        final LocalTime nextLure = LocalTime.now().plusMinutes(type.get().getDuration());
        final AlarmClock alarm = new AlarmClock(nextLure, () -> ready.set(true));
        alarm.start();
    }

    /**
     * @return - The type of the currently selected Lure.
     *         - NULL if no lure is selected.
     */
    public LureType getType()
    {
        return type.get();
    }

    /**
     * Checks if the lure is isReady to be applied.
     * @return - True the lure can be applied or not.
     */
    public boolean isReady()
    {
        return isSelected() && quantity.get() != EMPTY;
    }

    /**
     * Checks if a lure type is currently selected.
     * @return - True if a lure type is selected.
     */
    public boolean isSelected()
    {
        return type.get() != null;
    }

    /**
     * @return - Property of the type of lure that is being used.
     */
    public ObjectProperty<LureType> typeProperty()
    {
        return type;
    }

    @Override public String toString()
    {
        final LureType type = this.type.get();
        return type != null ? type.toString() : "";
    }
}
