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

package localization;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.Listener;

import java.util.ArrayList;
import java.util.List;

/** Storage class for output Strings. */
public final class Lang
{
    /** The default language if none is specified. */
    private static final Language DEFAULT_LANG = Language.ENGLISH;

    /** The current language being displayed. */
    private static final ObjectProperty<Language> activeLanguage = new SimpleObjectProperty<>(DEFAULT_LANG);
    /** Listeners who want to know about changes in the active language. */
    private static final List<Listener> listeners = new ArrayList<>();

    /* Prevent instantiation of this class. */
    private Lang()
    {
        /* If the language ever changes, notify the listeners. */
        activeLanguage.addListener((observable, oldValue, newValue) ->
                listeners.forEach(Listener::fire));
    }

    /**
     * Changes the active language of the program.
     * @param lang - Language to set.
     */
    public void changeLanguage(final Language lang)
    {
        assert lang != null;
        activeLanguage.set(lang);
    }

    /**
     * Adds a listener to the active language.
     * @param listener - Listener listening to changes in the active language.
     */
    public static void addListener(final Listener listener)
    {
        assert listener != null;
        listeners.add(listener);
    }

    /**
     * @return - Language property in read only format.
     */
    public static ReadOnlyObjectProperty<Language> activeLanguageProperty()
    {
        return activeLanguage;
    }

    /**
     * String library for each language.
     */
    public enum Locale
    {
        //
        CLOSE("Close"),
        SHINY_BAUBLE("Shiny Bauble"),
        NIGHTCRAWLERS("Nightcrawlers"),
        AQUADYNAMIC_FISH_LENS("Aquadynamic Fish Lens"),
        BRIGHT_BAUBLES("Bright Baubles"),
        FLESH_EATING_WORM("Flesh Eating Worm"),
        AQUADYNAMIC_FISH_ATTRACTOR("Aquadynamic Fish Attractor"),
        REPORT_ISSUE("Report Issue"),
        CONTACT_AUTHOR("Contact Author"),
        DONATE("Donate"),
        DRAW_RECTANGLE("Use your mouse to draw a small rectangle around the area where your [Fishing Bobber] tooltip will appear."),
        LURES_LEFT("Lures Remaining:"),
        QUIT_TIME("Stop/Logout at certain time?"),
        QUIT_EMPTY("Quit at zero lures?"),
        
        /* Labels. */
        LABEL_START("Start"),
        LABEL_STOP("Stop"),
        LABEL_CONSOLE("Console"),
        LABEL_SPEED("Scan Speed"),
        LABEL_TITLE("FishingBot"),
        LABEL_SENSITIVITY("Splash Sensitivity"),
        LABEL_ON_TOP("Always On Top"),
        LABEL_DEBUG("Debug Mode"),
        
        /* Tooltips. */
        TOOLTIP_OPTIONS("Options Menu"),
        TOOLTIP_START("Starts the fishing cycle."),
        TOOLTIP_STOP("Stops the fishing cycle."),
        TOOLTIP_ON_TOP("Toggles FishingBot on-top of all other windows."),
        TOOLTIP_DEBUG("Toggles debugging messages for the console."),
        //
        
        /* Messages. */
        MSG_BOBBER404("The Fishing Bobber was unable to be located."),
        MSG_CAUGHT("Fish caught successfully."),
        MSG_SPLASH404("Unable to locate the water's splash!"),
        MSG_START("Fishing will now begin. Please avoid using your mouse/keyboard."),
        MSG_END("Fishing will now end. This process may take a second."),
        MSG_CALIBRATED("Successfully located the Fishing Bobber tooltip."),
        //
        MSG_LURE_SET("Lure set. Next lure cycle will now use: "),
        MSG_LOGOUT_CONFIRM("Logout condition met. Ending fishing cycle and logging out!"),
        MSG_LURE_CHANGED("Lure changed. Next lure cycle will now use: "),
        MSG_LURE_REMOVE("Lure removed. No longer attempting to use any lure."),

        /* Formatted messages. */
        DEBUGF_SPLASH_DETECTION("[DEBUG] Water color changed by %.2f%%."),
        DEBUGF_SLEEP_MS("[DEBUG] Going to sleep for %d millisecond(s)."),
        DEBUGF_INTERRUPTED("[DEBUG] Sleep for %dms was interrupted %dms early."),
        MSGF_LURE_SWITCHED("Lure changed from %s to %s."),
        MSGF_LURE_APPLY("Applying %s to the user's fishing pole.");

        private final String[] msg;

        Locale(final String... msg)
        {
            assert msg != null;
            assert msg.length == Language.values().length;
            this.msg = msg;
        }

        /**
         * Retrieves this message associated with the active language.
         * @return - The message in the specified language.
         */
        public String get()
        {
            return msg[activeLanguage.get().ordinal()];
        }

        @Override public String toString()
        {
            return get();
        }
    }

    /**
     * Language Enum for language selection.
     */
    public enum Language
    {
        ENGLISH
    }
}
